package com.daimler.mbingresskit.implementation.network.model.agreement

import com.google.gson.annotations.SerializedName

data class ApiCiamAgreement(
    @SerializedName("href") val url: String,
    @SerializedName("documentId") val documentId: String,
    @SerializedName("version") val documentVersion: String,
    @SerializedName("displayName") val displayName: String?,
    @SerializedName("acceptanceState") val acceptedByUser: ApiAcceptedByUser?
)