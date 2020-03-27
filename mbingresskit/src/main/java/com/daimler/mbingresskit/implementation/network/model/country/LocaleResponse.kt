package com.daimler.mbingresskit.implementation.network.model.country

import com.google.gson.annotations.SerializedName

data class LocaleResponse(
    @SerializedName("localeCode") val localeCode: String?,
    @SerializedName("localeName") val localeName: String?
)