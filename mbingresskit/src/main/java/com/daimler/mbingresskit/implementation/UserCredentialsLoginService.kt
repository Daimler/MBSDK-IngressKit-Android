package com.daimler.mbingresskit.implementation

import com.daimler.mbloggerkit.MBLoggerKit
import com.daimler.mbingresskit.common.*
import com.daimler.mbingresskit.ingress.AuthstateRepository
import com.daimler.mbingresskit.login.*
import com.daimler.mbnetworkkit.networking.RequestError
import com.daimler.mbnetworkkit.networking.ResponseError
import com.daimler.mbnetworkkit.task.FutureTask
import com.daimler.mbnetworkkit.task.Task
import com.daimler.mbnetworkkit.task.TaskObject

class UserCredentialsLoginService(
    private val authstateRepository: AuthstateRepository,
    private val userCredentials: UserCredentials,
    private val tokenRepository: TokenRepository,
    private val deviceId: String,
    private val clientId: String,
    initialLoginState: UserCredentialsLoginState = UserCredentialsLoginState.LoggedOut
) : LoginService, LoginStateService, LoginActionHandler {

    private var loginProcess: LoginProcess = LoginProcess(this, initialLoginState)

    private var loginTask: Task<Void?, ResponseError<out RequestError>?> = TaskObject()

    private var logoutTask: Task<Void?, Void?> = TaskObject()

    // LoginService START

    override fun startLogin(): FutureTask<Void?, ResponseError<out RequestError>?> {
        loginTask = TaskObject()
        loginProcess.login()
        return loginTask.futureTask()
    }

    override fun startLogout(): FutureTask<Void?, Void?> {
        logoutTask = TaskObject()
        // We always confirm the logout here.
        tokenRepository.logout(clientId,
            authstateRepository.readAuthState().getToken().refreshToken)
            .onAlways { _, _, _ -> logoutConfirmed() }
        return logoutTask.futureTask()
    }

    // LoginService END

    // LoginStateService START

    override fun authorizationStarted() {
        // Should not be called as long as UserCredentialsLoginState triggers token request after login is started -> maybe throw exception to ensure for not beeing called
    }

    override fun receivedAuthResponse(authResponse: AuthorizationResponse?, authException: AuthorizationException?) {
        // Should not be called as long as UserCredentialsLoginState triggers token request after login is started -> maybe throw exception to ensure for not beeing called
    }

    override fun loginCancelled() {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun logoutConfirmed() {
        logoutTask.complete(null)
        loginProcess.logout()
    }

    // LoginEndService END

    // LoginActionHandler START

    override fun authorize() {
        // Should not be called as long as UserCredentialsLoginState triggers token request after login is started
    }

    override fun requestToken() {
        MBLoggerKit.d("Request Token: clientId=$clientId, user=${userCredentials.userName}")
        tokenRepository.requestToken(clientId, deviceId = deviceId, userName = userCredentials.userName, password = userCredentials.password)
            .onComplete {
                MBLoggerKit.d("KeycloadApi call to request token finished: $it")
                val authState = authstateRepository.readAuthState()
                authState.update(it)
                authstateRepository.saveAuthState(authState)
                MBLoggerKit.d("Updated Token in Authstate ")
                loginProcess.tokenReceived()
            }
            .onFailure {
                MBLoggerKit.d("KeycloakApi call to request token failed: ${it?.requestError}")
                loginTask.fail(it)
            }
    }

    override fun refreshToken(): FutureTask<Token, Throwable?> {
        val deferredTask = TaskObject<Token, Throwable?>()
        tokenRepository.refreshToken(clientId, refreshToken = authstateRepository.readAuthState().getToken().refreshToken)
            .onFailure {
                MBLoggerKit.d("KeycloakApi call to refresh token failed: $it")
                deferredTask.fail(it)
            }.onComplete {
                MBLoggerKit.d("KeycloakApi call to refresh token finished: $it")
                val authState = authstateRepository.readAuthState()
                authState.update(it)
                authstateRepository.saveAuthState(authState)
                MBLoggerKit.d("Updated Token in Authstate ")
                deferredTask.complete(it)
            }
        return deferredTask.futureTask()
    }

    override fun finishLogin() {
        val authState = authstateRepository.readAuthState()
        val tokenState = AuthenticationStateTokenState(authState).getTokenState()
        if (authState.isAuthorized()) {
            MBLoggerKit.d("Login finished: current state = ${tokenState.name}")
            loginTask.complete(null)
        } else {
            MBLoggerKit.d("Login failed: current state = ${tokenState.name}")
            loginTask.fail(ResponseError.requestError(LoginFailure.AUTHORIZATION_FAILED))
        }
    }

    override fun finishLogout() {
        authstateRepository.saveAuthState(AuthenticationState())
    }

    // LoginActionHandler STO
}