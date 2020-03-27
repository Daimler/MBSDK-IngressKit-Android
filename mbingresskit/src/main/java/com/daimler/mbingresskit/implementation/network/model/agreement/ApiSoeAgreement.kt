package com.daimler.mbingresskit.implementation.network.model.agreement

import com.google.gson.annotations.SerializedName

data class ApiSoeAgreement(
    @SerializedName("href") val url: String,
    @SerializedName("documentId") val documentId: String,
    @SerializedName("version") val documentVersion: String?,
    @SerializedName("position") val position: Int,
    @SerializedName("displayName") val displayName: String?,
    @SerializedName("acceptanceState") val acceptedByUser: ApiAcceptedByUser?,
    @SerializedName("isGeneralUserAgreement") val isGeneralUserAgreement: Boolean,
    @SerializedName("checkBoxText") val checkBoxText: String?,
    @SerializedName("titleText") val titleText: String?
)