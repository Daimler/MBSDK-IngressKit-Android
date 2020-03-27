package com.daimler.mbingresskit.login

import com.daimler.mbingresskit.common.JwtToken
import com.daimler.mbingresskit.common.Token

interface AuthenticationService {
    fun getTokenState(): TokenState
    fun needsTokenRefresh(): Boolean
    fun isValidJwtToken(jwtToken: JwtToken): Boolean
    fun getToken(): Token
    fun forceTokenRefresh()

    fun isLoggedIn(): Boolean {
        val state = getTokenState()
        return state is TokenState.LOGGEDIN || state is TokenState.AUTHORIZED
    }
}