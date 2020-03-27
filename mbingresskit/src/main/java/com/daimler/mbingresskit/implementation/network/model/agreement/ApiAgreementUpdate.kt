package com.daimler.mbingresskit.implementation.network.model.agreement

import com.google.gson.annotations.SerializedName

data class ApiAgreementUpdate(
    @SerializedName("documentId") val documentId: String,
    @SerializedName("version") val version: String,
    @SerializedName("acceptanceState") val accepted: Boolean,
    @SerializedName("acceptedLocale") val acceptedLocale: String
)