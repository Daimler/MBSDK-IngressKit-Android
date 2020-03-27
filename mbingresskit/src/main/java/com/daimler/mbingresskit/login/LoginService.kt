package com.daimler.mbingresskit.login

import com.daimler.mbnetworkkit.networking.RequestError
import com.daimler.mbnetworkkit.networking.ResponseError
import com.daimler.mbnetworkkit.task.FutureTask

interface LoginService : LoginStateService, RefreshTokenService {
    fun startLogin(): FutureTask<Void?, ResponseError<out RequestError>?>
    fun startLogout(): FutureTask<Void?, Void?>
}
