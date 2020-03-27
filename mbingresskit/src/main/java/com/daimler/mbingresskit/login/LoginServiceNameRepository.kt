package com.daimler.mbingresskit.login

interface LoginServiceNameRepository {
    fun saveLoginServiceName(loginService: LoginService)
    fun loadLoginServiceName(): String
    fun clearLoginServiceName()
}