package com.daimler.mbingresskit.implementation.network

import com.daimler.mbingresskit.common.LdssoUserAgreement
import com.daimler.mbingresskit.common.UserAgreementContentType
import com.daimler.mbingresskit.implementation.network.model.agreement.ApiAgreementsResponse
import com.daimler.mbingresskit.implementation.network.model.agreement.ApiLdssoAgreement

internal class LdssoAgreementTask(
    locale: String,
    country: String,
    api: UserApi,
    callback: Callback<LdssoUserAgreement>?
) : BaseAgreementTask<ApiLdssoAgreement,
        LdssoUserAgreement>("", locale, country, api, callback) {

    override fun getAgreements(agreements: ApiAgreementsResponse): List<ApiLdssoAgreement> =
            agreements.ldsso ?: emptyList()

    override fun fetchAgreementContent(agreement: ApiLdssoAgreement): LdssoUserAgreement {
        val url = agreement.href
        val type = if (url.endsWith(".pdf")) {
            UserAgreementContentType.PDF
        } else {
            UserAgreementContentType.WEB
        }
        val call = api.fetchPdfAgreementsContent(agreement.href)
        val response = call.execute()
        return if (response.isSuccessful) {
            ldssoAgreement(agreement, response.body()?.bytes(), type)
        } else {
            ldssoAgreement(agreement, null, type)
        }
    }

    private fun ldssoAgreement(apiAgreement: ApiLdssoAgreement, bytes: ByteArray?, contentType: UserAgreementContentType) =
            LdssoUserAgreement(
                    documentId = apiAgreement.id,
                    originalUrl = apiAgreement.href,
                    documentVersion = apiAgreement.version,
                    displayName = apiAgreement.displayName,
                    locale = locale,
                    countryCode = country,
                    filePath = null,
                    acceptedByUser = mapApiAcceptanceStateToAcceptanceState(apiAgreement.acceptedByUser),
                    implicitConsent = apiAgreement.implicitConsent == true,
                    fileContent = bytes,
                    position = apiAgreement.position,
                    contentType = contentType
            )
}