package com.daimler.mbingresskit

import android.annotation.SuppressLint
import com.daimler.mbingresskit.common.*
import com.daimler.mbingresskit.implementation.SharedCiamPrefs
import com.daimler.mbingresskit.implementation.SsoAccountPrefs
import com.daimler.mbingresskit.login.AuthenticationService
import com.daimler.mbingresskit.login.LoginStateService
import com.daimler.mbingresskit.login.UserService
import com.daimler.mbingresskit.login.jwt.JWTDecodeTokenValidator
import com.daimler.mbnetworkkit.networking.RequestError
import com.daimler.mbnetworkkit.networking.ResponseError
import com.daimler.mbnetworkkit.task.FutureTask

object MBIngressKit {

    @SuppressLint("StaticFieldLeak")
    private lateinit var serviceProxy: ServiceProxy

    fun init(ingressServiceConfig: IngressServiceConfig): MBIngressKit {
        serviceProxy = if (ingressServiceConfig.sharedUserId.isEmpty()) {
            val ciamPrefs = SharedCiamPrefs(ingressServiceConfig.context)
            ServiceProxy(
                ingressServiceConfig.context.applicationContext,
                ingressServiceConfig.sessionId,
                ingressServiceConfig.deviceUuid,
                ciamPrefs,
                ciamPrefs,
                JWTDecodeTokenValidator(),
                ingressServiceConfig.authUrl,
                ingressServiceConfig.userUrl,
                ingressServiceConfig.clientId,
                ingressStage = ingressServiceConfig.ingressStage,
                headerService = ingressServiceConfig.headerService,
                sessionExpiredHandler = ingressServiceConfig.sessionExpiredHandler,
                userAgreementsCache = ingressServiceConfig.userAgreementsCache,
                userCache = ingressServiceConfig.userCache,
                profileFieldsCache = ingressServiceConfig.profileFieldsCache,
                countryCache = ingressServiceConfig.countryCache,
                pinningErrorProcessor = ingressServiceConfig.pinningErrorProcessor,
                pinningConfigurations = ingressServiceConfig.pinningConfigurations
            )
        } else {
            val ssoPrefs = SsoAccountPrefs(ingressServiceConfig.context,
                ingressServiceConfig.sharedUserId, ingressServiceConfig.keyStoreAlias)
            ServiceProxy(
                ingressServiceConfig.context.applicationContext,
                ingressServiceConfig.sessionId,
                ingressServiceConfig.deviceUuid,
                ssoPrefs,
                ssoPrefs,
                JWTDecodeTokenValidator(),
                ingressServiceConfig.authUrl,
                ingressServiceConfig.userUrl,
                ingressServiceConfig.clientId,
                ingressStage = ingressServiceConfig.ingressStage,
                headerService = ingressServiceConfig.headerService,
                sessionExpiredHandler = ingressServiceConfig.sessionExpiredHandler,
                userAgreementsCache = ingressServiceConfig.userAgreementsCache,
                userCache = ingressServiceConfig.userCache,
                profileFieldsCache = ingressServiceConfig.profileFieldsCache,
                countryCache = ingressServiceConfig.countryCache,
                pinningErrorProcessor = ingressServiceConfig.pinningErrorProcessor,
                pinningConfigurations = ingressServiceConfig.pinningConfigurations
            )
        }
        return this
    }

    /**
     * Starts a login with Users credentials.
     * The response error is either a NetworkError, a LoginFailure or an HttpError.
     */
    fun loginWithCredentials(userCredentials: UserCredentials): FutureTask<Void?, ResponseError<out RequestError>?> {
        checkServiceInitialized()
        return serviceProxy.login(userCredentials)
    }

    fun logout(): FutureTask<Void?, Void?> {
        checkServiceInitialized()
        return serviceProxy.startLogout()
    }

    fun refreshTokenIfRequired(): FutureTask<Token, Throwable?> {
        checkServiceInitialized()
        return serviceProxy.refreshToken()
    }

    fun authenticationService(): AuthenticationService = checkServiceInitializedAndGetProxy()

    fun userService(): UserService = checkServiceInitializedAndGetProxy()

    private fun checkServiceInitializedAndGetProxy(): ServiceProxy {
        checkServiceInitialized()
        return serviceProxy
    }

    fun loginStateService(): LoginStateService = serviceProxy

    fun cachedCiamAgreements(locale: String, countryCode: String): UserAgreements<CiamUserAgreement>? {
        checkServiceInitialized()
        return serviceProxy.userAgreementsCache.readCiamAgreements(locale, countryCode)
    }

    fun cachedSoeAgreements(locale: String, countryCode: String): UserAgreements<SoeUserAgreement>? {
        checkServiceInitialized()
        return serviceProxy.userAgreementsCache.readSoeAgreements(locale, countryCode)
    }

    fun cachedNatconAgreements(locale: String, countryCode: String): UserAgreements<NatconUserAgreement>? {
        checkServiceInitialized()
        return serviceProxy.userAgreementsCache.readNatconAgreements(locale, countryCode)
    }

    fun cachedCustomAgreements(locale: String, countryCode: String): UserAgreements<CustomUserAgreement>? {
        checkServiceInitialized()
        return serviceProxy.userAgreementsCache.readCustomAgreements(locale, countryCode)
    }

    fun cachedLdssoAgreements(locale: String, countryCode: String): UserAgreements<LdssoUserAgreement>? {
        checkServiceInitialized()
        return serviceProxy.userAgreementsCache.readLdssoAgreements(locale, countryCode)
    }

    fun cachedUser(): User? {
        checkServiceInitialized()
        return serviceProxy.userCache.loadUser()
    }

    fun cachedUserImageBytes(): ByteArray? {
        checkServiceInitialized()
        return serviceProxy.userCache.loadUserImage()
    }

    fun cachedProfileFields(countryCode: String, locale: String): ProfileFieldsData? {
        checkServiceInitialized()
        return serviceProxy.profileFieldsCache.loadProfileFields(countryCode, locale)
    }

    /**
     * Deletes all databases and file storages.
     */
    fun clearLocalCache() {
        checkServiceInitialized()
        serviceProxy.userAgreementsCache.clear()
        serviceProxy.userCache.clear()
        serviceProxy.profileFieldsCache.clear()
    }

    private fun checkServiceInitialized() {
        if (::serviceProxy.isInitialized.not()) throw CiamServiceNotInitializedException()
    }

    class CiamServiceNotInitializedException : IllegalStateException("MBIngressKit was not initialized!")
}