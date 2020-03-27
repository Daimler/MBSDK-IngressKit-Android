package com.daimler.mbingresskit.login

import com.daimler.mbingresskit.common.JwtToken
import com.daimler.mbingresskit.common.Token
import io.mockk.every
import io.mockk.spyk
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.jupiter.api.Test

internal class AuthenticationServiceTest {

    @Test
    fun isLoggedIn_stateAuthorized() {
        val authService = spyk<DummyAuthService> {
            every { getTokenState() } returns TokenState.AUTHORIZED
        }
        val isLoggedIn = authService.isLoggedIn()
        assertTrue(isLoggedIn)
    }

    @Test
    fun isLoggedIn_stateLoggedIn() {
        val authService = spyk<DummyAuthService> {
            every { getTokenState() } returns TokenState.LOGGEDIN
        }
        val isLoggedIn = authService.isLoggedIn()
        assertTrue(isLoggedIn)
    }

    @Test
    fun isLoggedIn_stateLoggedOut() {
        val authService = spyk<DummyAuthService> {
            every { getTokenState() } returns TokenState.LOGGEDOUT
        }
        val isLoggedIn = authService.isLoggedIn()
        assertFalse(isLoggedIn)
    }
}

private class DummyAuthService : AuthenticationService {
    override fun getTokenState(): TokenState {
        TODO("not implemented")
    }

    override fun needsTokenRefresh(): Boolean {
        TODO("not implemented")
    }

    override fun isValidJwtToken(jwtToken: JwtToken): Boolean {
        TODO("not implemented")
    }

    override fun getToken(): Token {
        TODO("not implemented")
    }

    override fun forceTokenRefresh() {
        TODO("not implemented")
    }
}