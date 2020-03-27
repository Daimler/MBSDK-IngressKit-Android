package com.daimler.mbingresskit.implementation.network

import com.daimler.mbingresskit.common.CustomUserAgreement
import com.daimler.mbingresskit.common.UserAgreementContentType
import com.daimler.mbingresskit.implementation.network.model.agreement.ApiAgreementsResponse
import com.daimler.mbingresskit.implementation.network.model.agreement.ApiCustomAgreement

internal class CustomAgreementTask(
    locale: String,
    country: String,
    api: UserApi,
    callback: Callback<CustomUserAgreement>?
) : BaseAgreementTask<ApiCustomAgreement, CustomUserAgreement>("", locale, country, api, callback) {

    override fun getAgreements(agreements: ApiAgreementsResponse): List<ApiCustomAgreement> =
            agreements.custom ?: emptyList()

    override fun fetchAgreementContent(agreement: ApiCustomAgreement): CustomUserAgreement {
        val url = agreement.href
        val type = if (url.endsWith(".pdf")) {
            UserAgreementContentType.PDF
        } else {
            UserAgreementContentType.WEB
        }
        val call = api.fetchPdfAgreementsContent(agreement.href)
        val response = call.execute()
        return if (response.isSuccessful) {
            customAgreement(agreement, response.body()?.bytes(), type)
        } else {
            customAgreement(agreement, null, type)
        }
    }

    private fun customAgreement(apiAgreement: ApiCustomAgreement, bytes: ByteArray?, contentType: UserAgreementContentType) =
            CustomUserAgreement(
                    documentId = apiAgreement.id,
                    originalUrl = apiAgreement.href,
                    documentVersion = apiAgreement.version,
                    displayName = apiAgreement.displayName,
                    locale = locale,
                    countryCode = country,
                    filePath = null,
                    acceptedByUser = mapApiAcceptanceStateToAcceptanceState(apiAgreement.acceptedByUser),
                    appId = apiAgreement.appId,
                    displayOrder = apiAgreement.position,
                    category = apiAgreement.category,
                    displayLocation = apiAgreement.displayLocation,
                    implicitConsent = apiAgreement.implicitConsent == true,
                    fileContent = bytes,
                    contentType = contentType
            )
}