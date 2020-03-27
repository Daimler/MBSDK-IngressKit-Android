package com.daimler.mbingresskit.implementation.network

import com.daimler.mbnetworkkit.networking.BaseRetrofitTask
import okhttp3.ResponseBody
import java.net.HttpURLConnection

internal class BiometricRetrofitTask : BaseRetrofitTask<ResponseBody, Unit>() {

    override fun onHandleResponseBody(body: ResponseBody?, responseCode: Int) {
        if (validResponse(responseCode)) {
            complete(Unit)
        } else {
            failEmptyBody(responseCode)
        }
    }

    private fun validResponse(responseCode: Int) =
            responseCode in arrayOf(
                    HttpURLConnection.HTTP_OK,
                    HttpURLConnection.HTTP_ACCEPTED,
                    HttpURLConnection.HTTP_NO_CONTENT
            )
}