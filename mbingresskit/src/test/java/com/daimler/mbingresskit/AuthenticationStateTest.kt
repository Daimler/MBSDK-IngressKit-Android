package com.daimler.mbingresskit

import com.daimler.mbingresskit.common.AuthenticationState
import com.daimler.mbingresskit.common.JwtToken
import com.daimler.mbingresskit.common.MINIMUM_TOKEN_REST_LIFETIME_IN_MS
import com.daimler.mbingresskit.common.Token
import com.daimler.mbingresskit.login.AuthenticationStateTokenState
import com.daimler.mbingresskit.login.TokenState
import com.daimler.mbingresskit.login.TokenStateService
import junit.framework.Assert.assertEquals
import org.junit.Assert
import org.junit.Test

class AuthenticationStateTest {
    @Test
    fun authenticationSuccess() {
        val authenticationState = AuthenticationState()
        authenticationState.update(Token(TOKEN_TYP_REFRESH, TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN, JwtToken(TEST_JWT_TOKEN, ""), Long.MAX_VALUE, 0))
        Assert.assertTrue(authenticationState.isAuthorized())
    }

    @Test
    fun sameAccessToken() {
        val authenticationState = AuthenticationState()
        authenticationState.update(Token(TOKEN_TYP_REFRESH, TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN, JwtToken(TEST_JWT_TOKEN, ""), 0, 0))
        Assert.assertEquals(TEST_ACCESS_TOKEN, authenticationState.getToken().accessToken)
    }

    @Test
    fun sameRefreshToken() {
        val authenticationState = AuthenticationState()
        authenticationState.update(Token(TOKEN_TYP_REFRESH, TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN, JwtToken(TEST_JWT_TOKEN, ""), 0, 0))
        Assert.assertEquals(TEST_REFRESH_TOKEN, authenticationState.getToken().refreshToken)
    }

    @Test
    fun notAuthorizedBecauseOfNoToken() {
        val authenticationState = AuthenticationState()
        Assert.assertFalse(authenticationState.isAuthorized())
    }

    @Test
    fun validRefreshTokenBecauseItsAnOfflineToken() {
        val authenticationState = AuthenticationState()
        authenticationState.update(Token(TOKEN_TYP_Offline, TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN, JwtToken(TEST_JWT_TOKEN, ""), Long.MAX_VALUE, 0))
        Assert.assertEquals(true, authenticationState.isValidRefreshToken())
    }

    @Test
    fun authorizedBecauseItsAnOfflineToken() {
        val authenticationState = AuthenticationState()
        authenticationState.update(Token(TOKEN_TYP_Offline, TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN, JwtToken(TEST_JWT_TOKEN, ""), Long.MAX_VALUE, 0))
        Assert.assertTrue(authenticationState.isAuthorized())
    }

    @Test
    fun notAuthorizedBecauseOfEmptyAccessToken() {
        val authenticationState = AuthenticationState()
        authenticationState.update(Token(TOKEN_TYP_REFRESH, "", TEST_REFRESH_TOKEN, JwtToken(TEST_JWT_TOKEN, ""), 0, 0))
        Assert.assertFalse(authenticationState.isAuthorized())
    }

    @Test
    fun notAuthorizedBecauseOfEmptyRefreshToken() {
        val authenticationState = AuthenticationState()
        authenticationState.update(Token(TOKEN_TYP_REFRESH, TEST_ACCESS_TOKEN, "", JwtToken(TEST_JWT_TOKEN, ""), 0, 0))
        Assert.assertFalse(authenticationState.isAuthorized())
    }

    @Test
    fun notAuthorizedBecauseOfEmptyJwtToken() {
        val authenticationState = AuthenticationState()
        authenticationState.update(Token(TOKEN_TYP_REFRESH, TEST_ACCESS_TOKEN, TEST_ACCESS_TOKEN, JwtToken("", ""), 0, 0))
        Assert.assertFalse(authenticationState.isAuthorized())
    }

    @Test
    fun tokenRefreshRequiredIfExpired() {
        val authenticationState = AuthenticationState()
        authenticationState.update(Token(TOKEN_TYP_REFRESH, TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN, JwtToken(TEST_JWT_TOKEN, ""), 0, 0))
        Assert.assertTrue(authenticationState.needsAccessTokenRefresh())
    }

    @Test
    fun tokenRefreshRequired30SecondsBeforeExpiration() {
        val authenticationState = AuthenticationState()
        authenticationState.update(Token(TOKEN_TYP_REFRESH, "", TEST_REFRESH_TOKEN, JwtToken(TEST_JWT_TOKEN, ""), System.currentTimeMillis() + 30000, Long.MAX_VALUE))
        Assert.assertTrue(authenticationState.needsAccessTokenRefresh())
    }

    @Test
    fun tokenRefreshNotRequiredIfValidForMoreThan60Seconds() {
        val authenticationState = AuthenticationState()
        authenticationState.update(Token(TOKEN_TYP_REFRESH, "", TEST_REFRESH_TOKEN, JwtToken(TEST_JWT_TOKEN, ""), System.currentTimeMillis() + MINIMUM_TOKEN_REST_LIFETIME_IN_MS + 1000, Long.MAX_VALUE))
        Assert.assertFalse(authenticationState.needsAccessTokenRefresh())
    }

    @Test
    fun tokenRefreshRequiredIfForced() {
        val authenticationState = AuthenticationState()
        authenticationState.forceTokenRefresh()
        Assert.assertTrue(authenticationState.needsAccessTokenRefresh())
    }

    @Test
    fun accessTokenNotExpired() {
        val authenticationState = AuthenticationState()
        authenticationState.update(Token(TOKEN_TYP_REFRESH, TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN, JwtToken(TEST_JWT_TOKEN, ""), Long.MAX_VALUE, Long.MAX_VALUE))
        Assert.assertTrue(authenticationState.isAuthorized())
    }

    @Test
    fun loggedOutNoToken() {
        val authenticationState = AuthenticationState()
        val tokenStateService: TokenStateService = AuthenticationStateTokenState(authenticationState)
        assertEquals(TokenState.LOGGEDOUT, tokenStateService.getTokenState())
    }

    @Test
    fun loggedOutExpiredRefreshToken() {
        val authenticationState = AuthenticationState()
        authenticationState.update(Token(TOKEN_TYP_REFRESH, TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN, JwtToken(TEST_JWT_TOKEN, ""), 0, 0))
        val tokenStateService: TokenStateService = AuthenticationStateTokenState(authenticationState)
        assertEquals(TokenState.LOGGEDOUT, tokenStateService.getTokenState())
    }

    @Test
    fun loggedOutEmptyRefreshToken() {
        val authenticationState = AuthenticationState()
        authenticationState.update(Token(TOKEN_TYP_REFRESH, TEST_ACCESS_TOKEN, "", JwtToken(TEST_JWT_TOKEN, ""), 0, Long.MAX_VALUE))
        val tokenStateService: TokenStateService = AuthenticationStateTokenState(authenticationState)
        assertEquals(TokenState.LOGGEDOUT, tokenStateService.getTokenState())
    }

    @Test
    fun authorizedState() {
        val authenticationState = AuthenticationState()
        authenticationState.update(Token(TOKEN_TYP_REFRESH, TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN, JwtToken(TEST_JWT_TOKEN, ""), Long.MAX_VALUE, Long.MAX_VALUE))
        val tokenStateService: TokenStateService = AuthenticationStateTokenState(authenticationState)
        assertEquals(TokenState.AUTHORIZED, tokenStateService.getTokenState())
    }

    @Test
    fun loggedInExpiredAccessToken() {
        val authenticationState = AuthenticationState()
        authenticationState.update(Token(TOKEN_TYP_REFRESH, TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN, JwtToken(TEST_JWT_TOKEN, ""), 0, Long.MAX_VALUE))
        val tokenStateService: TokenStateService = AuthenticationStateTokenState(authenticationState)
        assertEquals(TokenState.LOGGEDIN, tokenStateService.getTokenState())
    }

    @Test
    fun loggedInEmptyAccessToken() {
        val authenticationState = AuthenticationState()
        authenticationState.update(Token(TOKEN_TYP_REFRESH, "", TEST_REFRESH_TOKEN, JwtToken(TEST_JWT_TOKEN, ""), Long.MAX_VALUE, Long.MAX_VALUE))
        val tokenStateService: TokenStateService = AuthenticationStateTokenState(authenticationState)
        assertEquals(TokenState.LOGGEDIN, tokenStateService.getTokenState())
    }

    companion object {
        private const val TEST_ACCESS_TOKEN = "1234asdf5678jklö"
        private const val TEST_REFRESH_TOKEN = "asdf1234jklö5678"
        private const val TEST_JWT_TOKEN = "asdfjklöasdfjklö"
        private const val TOKEN_TYP_REFRESH = "Refresh"
        private const val TOKEN_TYP_Offline = "Offline"
    }
}