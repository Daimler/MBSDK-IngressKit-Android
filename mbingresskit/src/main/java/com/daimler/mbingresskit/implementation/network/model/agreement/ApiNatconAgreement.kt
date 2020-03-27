package com.daimler.mbingresskit.implementation.network.model.agreement

import com.google.gson.annotations.SerializedName

data class ApiNatconAgreement(
    @SerializedName("version") val version: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("isMandatory") val isMandatory: Boolean,
    @SerializedName("position") val position: Int,
    @SerializedName("termsId") val termsId: String,
    @SerializedName("text") val text: String,
    @SerializedName("href") val url: String,
    @SerializedName("acceptedByUser") val acceptedByUser: ApiAcceptedByUser?
)