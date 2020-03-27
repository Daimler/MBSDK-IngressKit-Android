package com.daimler.mbingresskit.implementation.network

import com.daimler.mbnetworkkit.networking.BaseRetrofitTask
import okhttp3.ResponseBody
import java.net.HttpURLConnection

class PinManipulatingRetrofitTask : BaseRetrofitTask<ResponseBody, Unit>() {

    override fun onHandleResponseBody(body: ResponseBody?, responseCode: Int) {
        if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
            complete(Unit)
        } else {
            body?.let {
                complete(Unit)
            } ?: failEmptyBody(responseCode)
        }
    }
}