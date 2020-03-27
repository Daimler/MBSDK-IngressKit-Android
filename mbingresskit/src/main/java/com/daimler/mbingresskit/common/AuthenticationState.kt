package com.daimler.mbingresskit.common

const val MINIMUM_TOKEN_REST_LIFETIME_IN_MS = 60000

class AuthenticationState {

    private var token: Token = Token("", "", "", JwtToken("", ""), 0, 0)

    private var authorizationResponse: AuthorizationResponse? = null

    private var authorizationException: AuthorizationException? = null

    private var tokenRefreshRequired = false

    val TYP_OFFLINE_TOKEN = "Offline"

    fun update(authorizationResponse: AuthorizationResponse?, authorizationException: AuthorizationException? = null) {
        this.authorizationResponse = authorizationResponse
        this.authorizationException = authorizationException
    }

    fun update(token: Token) {
        this.token = token
        tokenRefreshRequired = false
    }

    fun lastAuthorizationResponse() = authorizationResponse

    fun isAuthorized(): Boolean {
        return (authorizationException == null).and(token.accessToken.isNotEmpty()).and(needsAccessTokenRefresh().not())
    }

    fun forceTokenRefresh() {
        tokenRefreshRequired = true
    }

    fun needsAccessTokenRefresh() = tokenRefreshRequired.or(token.tokenExpirationDate - MINIMUM_TOKEN_REST_LIFETIME_IN_MS <= System.currentTimeMillis())

    fun isValidRefreshToken(): Boolean {
        if (token.typ == TYP_OFFLINE_TOKEN) {
            return true
        } else {
            return token.refreshToken.isNotEmpty().and(token.refreshTokenExpirationDate >= System.currentTimeMillis())
        }
    }

    fun getToken() = token

    fun authorizationCode() = authorizationResponse?.authorizationCode ?: ""
}