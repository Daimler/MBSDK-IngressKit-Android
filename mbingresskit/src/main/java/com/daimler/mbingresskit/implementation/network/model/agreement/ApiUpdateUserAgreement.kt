package com.daimler.mbingresskit.implementation.network.model.agreement

import com.google.gson.annotations.SerializedName

data class ApiUpdateUserAgreement(
    @SerializedName("userAgreementId") val userAgreementId: String,
    @SerializedName("userAgreementVersionId") val userAgreementVersionId: Int,
    @SerializedName("userAgreementAcceptanceStatus") val acceptanceStatus: Boolean,
    @SerializedName("acceptedLocale") val acceptedLocale: String
)