package com.daimler.mbingresskit.login

import com.daimler.mbingresskit.common.AuthorizationException
import com.daimler.mbingresskit.common.AuthorizationResponse

interface LoginStateService {
    fun authorizationStarted()
    fun receivedAuthResponse(authResponse: AuthorizationResponse?, authException: AuthorizationException?)
    fun loginCancelled()
    fun logoutConfirmed()
}