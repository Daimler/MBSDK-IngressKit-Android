package com.daimler.mbingresskit.implementation.network.model.country

import com.google.gson.annotations.SerializedName

data class CountryResponse(
    @SerializedName("connectCountry") val connectCountry: Boolean,
    @SerializedName("countryCode") val countryCode: String,
    @SerializedName("countryName") val countryName: String,
    @SerializedName("instance") val instance: ApiCountryInstance?,
    @SerializedName("legalRegion") val legalRegion: String,
    @SerializedName("defaultLocale") val defaultLocale: String?,
    @SerializedName("locales") val locales: List<LocaleResponse>?,
    @SerializedName("natconCountry") val natconCountry: Boolean,
    @SerializedName("availability") val availability: Boolean
)