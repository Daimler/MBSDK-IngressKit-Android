package com.daimler.mbingresskit.implementation.network

import com.auth0.android.jwt.DecodeException
import com.auth0.android.jwt.JWT
import com.daimler.mbingresskit.common.Token
import com.daimler.mbingresskit.implementation.network.model.ApiKeycloakError
import com.daimler.mbingresskit.implementation.network.model.ApiKeycloakErrorResponse
import com.daimler.mbingresskit.implementation.network.model.KeycloakTokenResponse
import com.daimler.mbingresskit.login.LoginFailure
import com.daimler.mbingresskit.login.TokenRepository
import com.daimler.mbingresskit.login.jwt.DecodeJwtTokenException
import com.daimler.mbingresskit.util.toJwtToken
import com.daimler.mbloggerkit.MBLoggerKit
import com.daimler.mbnetworkkit.networking.RequestError
import com.daimler.mbnetworkkit.networking.ResponseError
import com.daimler.mbnetworkkit.networking.RetrofitTask
import com.daimler.mbnetworkkit.networking.defaultErrorMapping
import com.daimler.mbnetworkkit.task.FutureTask
import com.daimler.mbnetworkkit.task.TaskObject
import java.util.concurrent.TimeUnit

class KeycloakTokenRepository(
    private val keycloakApi: KeycloakApi,
    private val stage: String
) : TokenRepository {

    override fun requestToken(clientId: String, deviceId: String, userName: String, password: String): FutureTask<Token, ResponseError<out RequestError>?> {
        val tokenTask = RetrofitTask<KeycloakTokenResponse>()
        val deferredTokenTask = TaskObject<Token, ResponseError<out RequestError>?>()
        tokenTask.futureTask()
            .onComplete {
                deferredTokenTask.complete(mapTokenResponseToToken(it))
            }.onFailure {
                deferredTokenTask.fail(mapRequestTokenError(it))
            }
        keycloakApi.requestToken(stage, deviceId = deviceId, clientId = clientId, userName = userName, password = password).enqueue(tokenTask)
        return deferredTokenTask.futureTask()
    }

    override fun refreshToken(clientId: String, refreshToken: String): FutureTask<Token, Throwable?> {
        val tokenTask = RetrofitTask<KeycloakTokenResponse>()
        val deferredTokenTask = TaskObject<Token, Throwable?>()
        tokenTask.futureTask()
            .onComplete {
                deferredTokenTask.complete(mapTokenResponseToToken(it))
            }.onFailure {
                deferredTokenTask.fail(it)
            }
        keycloakApi.refreshToken(stage, clientId, refreshToken = refreshToken).enqueue(tokenTask)
        return deferredTokenTask.futureTask()
    }

    override fun logout(clientId: String, refreshToken: String): FutureTask<Unit, ResponseError<out RequestError>?> {
        val logoutTask = LogoutRetrofitTask()
        val deferredTask = TaskObject<Unit, ResponseError<out RequestError>?>()
        logoutTask.futureTask()
            .onComplete { deferredTask.complete(Unit) }
            .onFailure { deferredTask.fail(mapLogoutError(it)) }
        keycloakApi.logout(stage, clientId, refreshToken).enqueue(logoutTask)
        return deferredTask.futureTask()
    }

    private fun mapTokenResponseToToken(tokenResponse: KeycloakTokenResponse): Token {
        val encodedJwtToken = tokenResponse.accessToken.toJwtToken()
        if (encodedJwtToken.plainToken.isEmpty()) {
            MBLoggerKit.d("Received empty JWT-Token")
            throw DecodeJwtTokenException(tokenResponse.accessToken)
        } else {
            val encodedRefreshToken = tokenResponse.refreshToken.toJwtToken()
            if (encodedRefreshToken.plainToken.isEmpty()) {
                MBLoggerKit.d("Received empty Refresh Token")
                throw DecodeJwtTokenException(tokenResponse.refreshToken)
            }
            return try {
                JWT(encodedRefreshToken.plainToken).getClaim("typ").asString()?.let { typ ->
                    Token(
                        typ,
                        tokenResponse.accessToken,
                        tokenResponse.refreshToken,
                        tokenResponse.accessToken.toJwtToken(),
                        expirationDateForExpiresIn(tokenResponse.expiresIn),
                        expirationDateForExpiresIn(tokenResponse.refreshExpiresIn),
                        tokenResponse.scope)
                } ?: throw DecodeJwtTokenException(tokenResponse.refreshToken)
            } catch (e: DecodeException) {
                MBLoggerKit.d("Could not extract typ from refresh token")
                throw DecodeJwtTokenException(tokenResponse.refreshToken)
            }
        }
    }

    private fun mapRequestTokenError(error: Throwable?): ResponseError<out RequestError> {
        val defaultError = defaultErrorMapping(error, ApiKeycloakErrorResponse::class.java)
        return if (defaultError.requestError is ApiKeycloakErrorResponse) {
            val keycloakError = defaultError.requestError as ApiKeycloakErrorResponse
            val failure = when (keycloakError.error) {
                ApiKeycloakError.INVALID_REQUEST -> LoginFailure.AUTHORIZATION_FAILED
                ApiKeycloakError.INVALID_CLIENT -> LoginFailure.AUTHORIZATION_FAILED
                ApiKeycloakError.INVALID_GRANT -> LoginFailure.WRONG_CREDENTIALS
                ApiKeycloakError.INVALID_SCOPE -> LoginFailure.AUTHORIZATION_FAILED
                ApiKeycloakError.UNAUTHORIZED_CLIENT -> LoginFailure.AUTHORIZATION_FAILED
                ApiKeycloakError.UNSUPPORTED_GRANT_TYPE -> LoginFailure.AUTHORIZATION_FAILED
                null -> LoginFailure.AUTHORIZATION_FAILED
            }
            ResponseError.requestError(failure)
        } else {
            defaultError
        }
    }

    private fun mapLogoutError(error: Throwable?): ResponseError<out RequestError> {
        return defaultErrorMapping(error)
    }

    private fun expirationDateForExpiresIn(expiresIn: Int): Long {
        return System.currentTimeMillis() + expiresIn * TimeUnit.SECONDS.toMillis(1)
    }
}
