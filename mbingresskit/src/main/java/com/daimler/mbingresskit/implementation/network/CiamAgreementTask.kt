package com.daimler.mbingresskit.implementation.network

import com.daimler.mbingresskit.common.CiamUserAgreement
import com.daimler.mbingresskit.implementation.network.model.agreement.ApiAgreementsResponse
import com.daimler.mbingresskit.implementation.network.model.agreement.ApiCiamAgreement
import com.daimler.mbingresskit.implementation.network.model.agreement.ApiHtmlAgreement

internal class CiamAgreementTask(
    locale: String,
    country: String,
    api: UserApi,
    callback: Callback<CiamUserAgreement>?
) : BaseAgreementTask<ApiCiamAgreement, CiamUserAgreement>("", locale, country, api, callback) {

    override fun getAgreements(agreements: ApiAgreementsResponse): List<ApiCiamAgreement> =
            agreements.ciam ?: emptyList()

    override fun fetchAgreementContent(agreement: ApiCiamAgreement): CiamUserAgreement {
        val call = api.fetchHtmlAgreementsContent(agreement.url)
        val response = call.execute()
        return if (response.isSuccessful) {
            userAgreement(agreement, response.body())
        } else {
            userAgreement(agreement, null)
        }
    }

    private fun userAgreement(apiAgreement: ApiCiamAgreement, apiHtmlContent: ApiHtmlAgreement?) =
            CiamUserAgreement(
                    apiAgreement.documentId,
                    apiAgreement.url,
                    apiAgreement.documentVersion,
                    apiHtmlContent?.title ?: apiAgreement.displayName,
                    locale,
                    country,
                    apiHtmlContent?.content,
                    null,
                    mapApiAcceptanceStateToAcceptanceState(apiAgreement.acceptedByUser)
            )
}