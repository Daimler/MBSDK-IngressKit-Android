package com.daimler.mbingresskit.implementation.network

import com.daimler.mbingresskit.common.SoeUserAgreement
import com.daimler.mbingresskit.implementation.network.model.agreement.ApiAgreementsResponse
import com.daimler.mbingresskit.implementation.network.model.agreement.ApiSoeAgreement

internal class SoeAgreementTask(
    jwtToken: String,
    locale: String,
    country: String,
    api: UserApi,
    callback: Callback<SoeUserAgreement>?
) : BaseAgreementTask<ApiSoeAgreement, SoeUserAgreement>(jwtToken, locale, country, api, callback) {

    override fun getAgreements(agreements: ApiAgreementsResponse): List<ApiSoeAgreement> =
            agreements.soe ?: emptyList()

    override fun fetchAgreementContent(agreement: ApiSoeAgreement): SoeUserAgreement {
        val call = api.fetchPdfAgreementsContent(agreement.url)
        val response = call.execute()
        return if (response.isSuccessful) {
            userAgreement(agreement, response.body()?.bytes())
        } else {
            userAgreement(agreement, null)
        }
    }

    private fun userAgreement(apiAgreement: ApiSoeAgreement, bytes: ByteArray?) =
            SoeUserAgreement(
                    apiAgreement.documentId,
                    apiAgreement.url,
                    apiAgreement.documentVersion,
                    apiAgreement.position,
                    apiAgreement.displayName,
                    locale,
                    country,
                    bytes,
                    null,
                    mapApiAcceptanceStateToAcceptanceState(apiAgreement.acceptedByUser),
                    apiAgreement.isGeneralUserAgreement,
                    apiAgreement.checkBoxText,
                    apiAgreement.titleText
            )
}