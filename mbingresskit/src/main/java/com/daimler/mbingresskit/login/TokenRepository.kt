package com.daimler.mbingresskit.login

import com.daimler.mbingresskit.common.Token
import com.daimler.mbnetworkkit.networking.RequestError
import com.daimler.mbnetworkkit.networking.ResponseError
import com.daimler.mbnetworkkit.task.FutureTask

interface TokenRepository {

    fun requestToken(clientId: String, deviceId: String, userName: String, password: String): FutureTask<Token, ResponseError<out RequestError>?>

    fun refreshToken(clientId: String, refreshToken: String): FutureTask<Token, Throwable?>

    fun logout(clientId: String, refreshToken: String): FutureTask<Unit, ResponseError<out RequestError>?>
}