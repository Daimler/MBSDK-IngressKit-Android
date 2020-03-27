package com.daimler.mbingresskit.implementation.network.model.agreement

import com.google.gson.annotations.SerializedName

data class ApiHtmlAgreement(
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("buttonCaption") val buttonCaption: String,
    @SerializedName("scope") val scope: String,
    @SerializedName("version") val version: String
)