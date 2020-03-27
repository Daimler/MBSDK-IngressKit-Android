package com.daimler.mbingresskit.implementation.network.model.agreement

import com.google.gson.annotations.SerializedName

data class ApiCustomAgreement(
    @SerializedName("id") val id: String,
    @SerializedName("appId") val appId: String,
    @SerializedName("version") val version: String,
    @SerializedName("category") val category: String,
    @SerializedName("displayLocation") val displayLocation: String,
    @SerializedName("position") val position: Int,
    @SerializedName("displayName") val displayName: String?,
    @SerializedName("implicitConsent") val implicitConsent: Boolean?,
    @SerializedName("href") val href: String,
    @SerializedName("acceptedByUser") val acceptedByUser: ApiAcceptedByUser?
)