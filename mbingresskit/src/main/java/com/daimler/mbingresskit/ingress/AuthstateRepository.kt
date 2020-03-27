package com.daimler.mbingresskit.ingress

import com.daimler.mbingresskit.common.AuthenticationState

interface AuthstateRepository {
    fun saveAuthState(authState: AuthenticationState)
    fun readAuthState(): AuthenticationState
    fun clearAuthState()
}