package com.daimler.mbingresskit.implementation.network

import com.daimler.mbingresskit.implementation.network.model.agreement.ApiAgreementsResponse
import com.daimler.mbingresskit.implementation.network.model.agreement.RawAgreementsResponse
import com.daimler.mbnetworkkit.networking.BaseRetrofitTask
import java.net.HttpURLConnection

internal class AgreementsRetrofitTask : BaseRetrofitTask<ApiAgreementsResponse, RawAgreementsResponse>() {

    override fun onHandleResponseBody(body: ApiAgreementsResponse?, responseCode: Int) {
        body?.let {
            val complete = responseCode != HttpURLConnection.HTTP_PARTIAL
            complete(RawAgreementsResponse(it, complete))
        } ?: failEmptyBody(responseCode)
    }
}