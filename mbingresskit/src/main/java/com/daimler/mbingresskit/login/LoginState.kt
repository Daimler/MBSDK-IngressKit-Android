package com.daimler.mbingresskit.login

interface LoginState {
    fun login(loginProcess: LoginProcess)
    fun authorized(loginProcess: LoginProcess)
    fun tokenReceived(loginProcess: LoginProcess)
    fun logout(loginProcess: LoginProcess)
}