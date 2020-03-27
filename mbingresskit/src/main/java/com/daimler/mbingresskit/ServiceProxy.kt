package com.daimler.mbingresskit

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import com.daimler.mbloggerkit.MBLoggerKit
import com.daimler.mbingresskit.common.*
import com.daimler.mbingresskit.implementation.UserCredentialsLoginService
import com.daimler.mbingresskit.implementation.network.KeycloakTokenRepository
import com.daimler.mbingresskit.implementation.network.RetrofitServiceProvider
import com.daimler.mbingresskit.implementation.network.RetrofitUserService
import com.daimler.mbingresskit.ingress.AuthstateRepository
import com.daimler.mbingresskit.login.*
import com.daimler.mbingresskit.login.jwt.JwtTokenValidator
import com.daimler.mbingresskit.persistence.CountryCache
import com.daimler.mbingresskit.persistence.ProfileFieldsCache
import com.daimler.mbingresskit.persistence.UserAgreementsCache
import com.daimler.mbingresskit.persistence.UserCache
import com.daimler.mbnetworkkit.certificatepinning.CertificateConfiguration
import com.daimler.mbnetworkkit.certificatepinning.CertificatePinnerFactory
import com.daimler.mbnetworkkit.certificatepinning.CertificatePinningErrorProcessor
import com.daimler.mbnetworkkit.header.HeaderService
import com.daimler.mbnetworkkit.networking.RequestError
import com.daimler.mbnetworkkit.networking.ResponseError
import com.daimler.mbnetworkkit.task.FutureTask
import com.daimler.mbnetworkkit.task.TaskObject
import java.util.concurrent.LinkedBlockingQueue

/**
 * Proxy class
 */
internal class ServiceProxy(
    private val context: Context,
    private val sessionId: String,
    private val deviceId: String,
    private val authstateRepository: AuthstateRepository,
    private val loginServiceNameRepository: LoginServiceNameRepository,
    private val jwtTokenValidator: JwtTokenValidator,
    authUrl: String,
    userUrl: String,
    private val clientId: String,
    ingressStage: String,
    enableLogging: Boolean = false,
    private val headerService: HeaderService,
    private val sessionExpiredHandler: SessionExpiredHandler?,
    val userAgreementsCache: UserAgreementsCache,
    val userCache: UserCache,
    val profileFieldsCache: ProfileFieldsCache,
    private val pinningErrorProcessor: CertificatePinningErrorProcessor?,
    private val pinningConfigurations: List<CertificateConfiguration>,
    countryCache: CountryCache,
    private val tokenRepository: TokenRepository = KeycloakTokenRepository(RetrofitServiceProvider.createKeycloakApi(
            context, authUrl, enableLogging, pinningErrorProcessor,
            CertificatePinnerFactory(), pinningConfigurations), ingressStage),
    private val userService: UserService = CachedUserService(RetrofitUserService(RetrofitServiceProvider.createUserApi(
            context, userUrl, enableLogging, headerService, pinningErrorProcessor,
            CertificatePinnerFactory(), pinningConfigurations),
            sessionId, headerService), userCache, userAgreementsCache, profileFieldsCache, countryCache, headerService
    )
) : LoginService, AuthenticationService, UserService {

    private val lastLoginService
        get() = loadLastLoginServiceIfLoggedIn()

    private var loginService: LoginService? = lastLoginService
        get() = if (field == null) lastLoginService else field

    private var loginStateService: LoginStateService? = lastLoginService

    private var loginTask: TaskObject<Void?, ResponseError<out RequestError>?> = TaskObject()

    private var logoutTask: TaskObject<Void?, Void?> = TaskObject()

    // holds task objects returned by refreshToken(); this is to support concurrent refresh requests
    private val refreshQueue = RefreshQueue()

    fun login(userCredentials: UserCredentials): FutureTask<Void?, ResponseError<out RequestError>?> {
        MBLoggerKit.d("Login with ${userCredentials::class.java.simpleName}")
        val loginService = createLoginServiceForName(UserCredentialsLoginService::class.java.simpleName,
                userCredentials = userCredentials, deviceId = deviceId)
        return startLoginIfNotInProgress(loginService, loginService)
    }

    private fun startLoginIfNotInProgress(loginService: LoginService, loginStateService: LoginStateService): FutureTask<Void?, ResponseError<out RequestError>?> {
        this.loginService = loginService
        this.loginStateService = loginStateService
        return startLogin()
    }

    private fun loadLastLoginServiceIfLoggedIn(): LoginService? {
        return if (getTokenState() != TokenState.LOGGEDOUT) {
            MBLoggerKit.d("User already logged in -> Load last LoginService")

            createLoginServiceForName(loginServiceNameRepository.loadLoginServiceName())
        } else {
            null
        }
    }

    private fun createLoginServiceForName(
        loginServiceName: String,
        userCredentials: UserCredentials = UserCredentials("", ""),
        deviceId: String = ""
    ): LoginService {
        // TODO: This change is a temporary fix for a very high number of 'UnknownLoginServiceException's
        // TODO: ... in production environment and should be cleaned as soon as CNG will be implemented
        MBLoggerKit.d("Created $loginServiceName")
        return UserCredentialsLoginService(
            authstateRepository,
            userCredentials,
            tokenRepository,
            deviceId,
            clientId
        )
    }

    // TODO: This function's only purpose is to get more details for a particular PlayStore crash
    // TODO: and must be reverted as soon as possible
    private fun createDebugginErrorMessage(context: Context, loginServiceName: String): String {
        val myStarInstalled = isAppInstalled("com.daimler.ris.mercedesme.ece.android")
        val myStoreInstalled = isAppInstalled("com.daimler.ris.store.ece.android")
        val myAssistantInstalled = isAppInstalled("com.daimler.ris.service.ece.android")

        return "LoginServiceName: $loginServiceName, " +
                "MyStar installed: $myStarInstalled, " +
                "Store installed: $myStoreInstalled, " +
                "Service installed: $myAssistantInstalled, " +
                "Current process: ${getProcessName(context)}"
    }

    private fun isAppInstalled(packageName: String) = try {
        context.packageManager.getPackageInfo(packageName, 0)
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }

    private fun getProcessName(context: Context): String? {
        val currentProcessId = android.os.Process.myPid()
        return (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).runningAppProcesses
                .firstOrNull { it.pid == currentProcessId }?.processName
    }

    // LoginService START

    override fun startLogin(): FutureTask<Void?, ResponseError<out RequestError>?> {
        MBLoggerKit.d("Start Login-Process")
        val deferredLoginTask = loginService?.startLogin() ?: throw NoLoginServiceDefinedException()
        deferredLoginTask.onComplete { result ->
            loginService?.let {
                loginServiceNameRepository.saveLoginServiceName(it)
            }
            MBLoggerKit.d("Login-Task completed")
            loginTask.complete(result)
        }.onFailure {
            loginService = null
            loginStateService = null
            MBLoggerKit.e("Login-Task failed: ${it?.requestError}")
            loginTask.fail(it)
        }.onAlways { _, _, _ ->
            loginTask = TaskObject()
        }
        return loginTask.futureTask()
    }

    override fun startLogout(): FutureTask<Void?, Void?> {
        MBLoggerKit.d("Start Logout-Process")
        val deferredLogoutTask = loginService?.startLogout()
                ?: throw NoLoginServiceDefinedException()
        deferredLogoutTask.onComplete {
            loginService?.let {
                loginServiceNameRepository.clearLoginServiceName()
            }
            MBLoggerKit.d("Logout-Task completed")
            logoutTask.complete(it)
        }.onFailure {
            MBLoggerKit.e("Logout-Task failed")
            logoutTask.fail(it)
        }.onAlways { _, _, _ ->
            loginService = null
            loginStateService = null
            logoutTask = TaskObject()
        }
        return logoutTask.futureTask()
    }

    // LoginService END

    // AuthenticationService START

    override fun getTokenState(): TokenState {
        val authState = authstateRepository.readAuthState()
        val currentTokenState = AuthenticationStateTokenState(authState).getTokenState()
        MBLoggerKit.d("getTokenState: ${currentTokenState.name}")
        MBLoggerKit.d("Current access token: ${authState.getToken().accessToken}")
        MBLoggerKit.d("Current refresh token: ${authState.getToken().refreshToken}")
        return currentTokenState
    }

    override fun needsTokenRefresh(): Boolean {
        // todo: maybe handle state if logged out and throw an exception
        return getTokenState() is TokenState.LOGGEDIN
    }

    override fun isValidJwtToken(jwtToken: JwtToken): Boolean {
        val isValidJwt = jwtTokenValidator.isValidToken(jwtToken).and(jwtTokenValidator.isExpired(jwtToken).not())
        MBLoggerKit.d("Checked JWT-Token -> valid=$isValidJwt")
        return isValidJwt
    }

    override fun getToken(): Token {
        val authSTate = authstateRepository.readAuthState()
        return authSTate.getToken()
    }

    // AuthenticationService END

    // LoginStateService START

    override fun authorizationStarted() = loginStateService?.authorizationStarted()
            ?: throw NoLoginServiceDefinedException()

    override fun receivedAuthResponse(authResponse: AuthorizationResponse?, authException: AuthorizationException?) = loginStateService?.receivedAuthResponse(authResponse, authException)
            ?: throw NoLoginServiceDefinedException()

    override fun loginCancelled() = loginStateService?.loginCancelled()
            ?: throw NoLoginServiceDefinedException()

    override fun logoutConfirmed() = loginStateService?.logoutConfirmed()
            ?: throw NoLoginServiceDefinedException()

    // LoginStateService END

    // RefreshTokenService START
    override fun forceTokenRefresh() {
        MBLoggerKit.d("Forced to refresh Token")
        val authState = authstateRepository.readAuthState().apply {
            forceTokenRefresh()
        }
        authstateRepository.saveAuthState(authState)
    }

    override fun refreshToken(): FutureTask<Token, Throwable?> {
        val handler = Handler(Looper.getMainLooper())
        return when (val state = getTokenState()) {
            is TokenState.LOGGEDOUT -> state.handleTokenRefresh(handler)
            is TokenState.LOGGEDIN -> state.handleTokenRefresh(handler, sessionExpiredHandler, refreshQueue, loginService)
            else -> {
                MBLoggerKit.d("refreshToken -> no refresh required")
                val tokenTask = TaskObject<Token, Throwable?>()
                handler.post { tokenTask.complete(getToken()) }
                tokenTask.futureTask()
            }
        }
    }

    // RefreshTokenService START

    // UserService START

    override fun sendPin(userName: String, countryCode: String): FutureTask<LoginUser, ResponseError<out RequestError>?> {
        MBLoggerKit.d("Send Pin for $userName in country $countryCode.")
        return userService.sendPin(userName, countryCode)
    }

    override fun loadUser(jwtToken: String): FutureTask<User, ResponseError<out RequestError>?> {
        MBLoggerKit.d("Load user: JWTToken=$jwtToken")
        return userService.loadUser(jwtToken)
    }

    override fun createUser(useMailAsUsername: Boolean, user: User): FutureTask<RegistrationUser, ResponseError<out RequestError>?> {
        MBLoggerKit.d("Create User: useMailAsUsername=$useMailAsUsername, user=$user")
        return userService.createUser(useMailAsUsername, user)
    }

    override fun updateUser(jwtToken: String, user: User): FutureTask<User, ResponseError<out RequestError>?> {
        MBLoggerKit.d("Update User: JWTToken=$jwtToken, $user ")
        return userService.updateUser(jwtToken, user)
    }

    override fun deleteUser(jwtToken: String, user: User): FutureTask<Unit, ResponseError<out RequestError>?> {
        MBLoggerKit.d("Delete User: ${user.email}")
        return userService.deleteUser(jwtToken, user)
    }

    override fun updateProfilePicture(jwtToken: String, bitmapByteArray: ByteArray, mediaType: String): FutureTask<Unit, ResponseError<out RequestError>?> {
        MBLoggerKit.d("Update Profile Picture")
        return userService.updateProfilePicture(jwtToken, bitmapByteArray, mediaType)
    }

    override fun fetchProfilePictureBytes(jwtToken: String): FutureTask<ByteArray, ResponseError<out RequestError>?> {
        MBLoggerKit.d("Fetch Profile Picture Bytes")
        return userService.fetchProfilePictureBytes(jwtToken)
    }

    override fun fetchCountries(): FutureTask<Countries, ResponseError<out RequestError>?> {
        MBLoggerKit.d("Fetch countries")
        return userService.fetchCountries()
    }

    override fun fetchNatconCountries(): FutureTask<List<String>, ResponseError<out RequestError>?> {
        MBLoggerKit.d("Fetch Natcon countries.")
        return userService.fetchNatconCountries()
    }

    override fun setPin(jwtToken: String, pin: String): FutureTask<Unit, ResponseError<out RequestError>?> {
        MBLoggerKit.d("Set Pin: $pin")
        return userService.setPin(jwtToken, pin)
    }

    override fun changePin(jwtToken: String, currentPin: String, newPin: String): FutureTask<Unit, ResponseError<out RequestError>?> {
        MBLoggerKit.d("Change Pin: currentPin = $currentPin, newPin = $newPin")
        return userService.changePin(jwtToken, currentPin, newPin)
    }

    override fun deletePin(jwtToken: String, currentPin: String): FutureTask<Unit, ResponseError<out RequestError>?> {
        MBLoggerKit.d("Delete Pin: $currentPin")
        return userService.deletePin(jwtToken, currentPin)
    }

    override fun resetPin(jwtToken: String): FutureTask<Unit, ResponseError<out RequestError>?> {
        MBLoggerKit.d("Reset Pin")
        return userService.resetPin(jwtToken)
    }

    override fun sendBiometricActivation(jwtToken: String, countryCode: String, state: UserBiometricState, currentPin: String?): FutureTask<Unit, ResponseError<out RequestError>?> {
        MBLoggerKit.d("Send biometric activation: $state")
        return userService.sendBiometricActivation(jwtToken, countryCode, state, currentPin)
    }

    override fun updateUnitPreferences(
        jwtToken: String,
        unitPreferences: UnitPreferences
    ): FutureTask<Unit, ResponseError<out RequestError>?> {
        MBLoggerKit.d("Update unit preferences: $unitPreferences")
        return userService.updateUnitPreferences(jwtToken, unitPreferences)
    }

    override fun updateAdaptionValues(jwtToken: String, bodyHeight: UserBodyHeight): FutureTask<Unit, ResponseError<out RequestError>?> {
        MBLoggerKit.d("Update adaption values: $bodyHeight")
        return userService.updateAdaptionValues(jwtToken, bodyHeight)
    }

    override fun fetchProfileFields(countryCode: String, locale: String?): FutureTask<ProfileFieldsData, ResponseError<out RequestError>?> {
        MBLoggerKit.d("Fetch profile fields: countryCode = $countryCode.")
        return userService.fetchProfileFields(countryCode, locale)
    }

    override fun fetchCiamTermsAndConditions(countryCode: String, locale: String?): FutureTask<UserAgreements<CiamUserAgreement>, ResponseError<out RequestError>?> {
        MBLoggerKit.d("Fetch CIAM Terms and Conditions: countryCode = $countryCode.")
        return userService.fetchCiamTermsAndConditions(countryCode, locale)
    }

    override fun fetchSOETermsAndConditions(jwtToken: String, country: String, locale: String?): FutureTask<UserAgreements<SoeUserAgreement>, ResponseError<out RequestError>?> {
        MBLoggerKit.d("Fetch SOE Terms and Conditions: countryCode = $country.")
        return userService.fetchSOETermsAndConditions(jwtToken, country, locale)
    }

    override fun fetchNatconTermsAndConditions(countryCode: String, locale: String?): FutureTask<UserAgreements<NatconUserAgreement>, ResponseError<out RequestError>?> {
        MBLoggerKit.d("Fetch Natcon Terms and Conditions: countryCode = $countryCode.")
        return userService.fetchNatconTermsAndConditions(countryCode, locale)
    }

    override fun fetchCustomTermsAndConditions(countryCode: String, jwtToken: String?, locale: String?): FutureTask<UserAgreements<CustomUserAgreement>, ResponseError<out RequestError>?> {
        MBLoggerKit.d("Fetch Custom Terms and Conditions: countryCode = $countryCode.")
        return userService.fetchCustomTermsAndConditions(countryCode, jwtToken, locale)
    }

    override fun fetchLdssoTermsAndConditions(countryCode: String, jwtToken: String?, ldssoAppId: String, ldssoVersionId: String, locale: String?): FutureTask<UserAgreements<LdssoUserAgreement>, ResponseError<out RequestError>?> {
        MBLoggerKit.d("Fetch LDSSO Terms and Conditions: countryCode = $countryCode.")
        return userService.fetchLdssoTermsAndConditions(countryCode, jwtToken, ldssoAppId, ldssoVersionId, locale)
    }

    override fun updateCiamAgreements(jwtToken: String, agreements: UserAgreementUpdates, locale: String?): FutureTask<Unit, ResponseError<out RequestError>?> {
        MBLoggerKit.d("Update Ciam Agreements.")
        return userService.updateCiamAgreements(jwtToken, agreements, locale)
    }

    override fun updateCustomAgreements(jwtToken: String, agreements: UserAgreementUpdates, locale: String?): FutureTask<Unit, ResponseError<out RequestError>?> {
        MBLoggerKit.d("Update custom agreements.")
        return userService.updateCustomAgreements(jwtToken, agreements, locale)
    }

    override fun updateLdssoAgreements(jwtToken: String, agreements: UserAgreementUpdates, ldssoAppId: String, ldssoVersionId: String, locale: String?): FutureTask<Unit, ResponseError<out RequestError>?> {
        MBLoggerKit.d("Update LDSSO agreements.")
        return userService.updateLdssoAgreements(jwtToken, agreements, ldssoAppId, ldssoVersionId, locale)
    }

    override fun updateNatconAgreements(jwtToken: String, agreements: UserAgreementUpdates, locale: String?): FutureTask<Unit, ResponseError<out RequestError>?> {
        MBLoggerKit.d("Update natcon agreements.")
        return userService.updateNatconAgreements(jwtToken, agreements, locale)
    }

    override fun updateSOEAgreements(jwtToken: String, agreements: UserAgreementUpdates, locale: String?): FutureTask<Unit, ResponseError<out RequestError>?> {
        MBLoggerKit.d("Update SOE agreements.")
        return userService.updateSOEAgreements(jwtToken, agreements, locale)
    }

    override fun verifyUser(jwtToken: String, scanReference: String): FutureTask<Unit, ResponseError<out RequestError>?> {
        MBLoggerKit.d("Verify user with scanReference: $scanReference")
        return userService.verifyUser(jwtToken, scanReference)
    }

    // UserService END

    inner class NoLoginServiceDefinedException : IllegalStateException("A LoginService must be set to start login or handle LoginStates")

    inner class UnknownLoginServiceException(loginServiceName: String) : IllegalArgumentException("Unknown LoginService: $loginServiceName")

    inner class NotLoggedInException : IllegalStateException("Cannot update token because currently not logged in"), RequestError

    inner class TooManyConcurrentRequestsException : IllegalStateException("Too many concurrent requests."), RequestError
}

typealias RefreshQueue = LinkedBlockingQueue<TaskObject<Token, Throwable?>>