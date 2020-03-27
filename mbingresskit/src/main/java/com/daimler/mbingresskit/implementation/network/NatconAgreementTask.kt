package com.daimler.mbingresskit.implementation.network

import com.daimler.mbingresskit.common.NatconUserAgreement
import com.daimler.mbingresskit.common.UserAgreementContentType
import com.daimler.mbingresskit.implementation.network.model.agreement.ApiAgreementsResponse
import com.daimler.mbingresskit.implementation.network.model.agreement.ApiNatconAgreement

internal class NatconAgreementTask(
    locale: String,
    countryCode: String,
    api: UserApi,
    callback: Callback<NatconUserAgreement>?
) : BaseAgreementTask<ApiNatconAgreement, NatconUserAgreement>("", locale, countryCode, api, callback) {

    override fun getAgreements(agreements: ApiAgreementsResponse): List<ApiNatconAgreement> =
            agreements.natcon ?: emptyList()

    override fun fetchAgreementContent(agreement: ApiNatconAgreement): NatconUserAgreement {
        val url = agreement.url
        return if (url.endsWith(".pdf")) {
            val call = api.fetchPdfAgreementsContent(agreement.url)
            val response = call.execute()
            if (response.isSuccessful) {
                userAgreement(agreement, response.body()?.bytes(), UserAgreementContentType.PDF)
            } else {
                userAgreement(agreement, null, UserAgreementContentType.PDF)
            }
        } else {
            userAgreement(agreement, null, UserAgreementContentType.WEB)
        }
    }

    private fun userAgreement(apiAgreement: ApiNatconAgreement, bytes: ByteArray?, type: UserAgreementContentType) =
            NatconUserAgreement(
                    apiAgreement.termsId,
                    apiAgreement.url,
                    apiAgreement.version,
                    apiAgreement.title,
                    locale,
                    country,
                    null,
                    mapApiAcceptanceStateToAcceptanceState(apiAgreement.acceptedByUser),
                    apiAgreement.description,
                    apiAgreement.text,
                    apiAgreement.isMandatory,
                    apiAgreement.position,
                    bytes,
                    type
            )
}