package com.daimler.mbingresskit.implementation.network.model.agreement

import com.google.gson.annotations.SerializedName

data class ApiLdssoAgreement(
    @SerializedName("id") val id: String,
    @SerializedName("locale") val locale: String,
    @SerializedName("version") val version: String,
    @SerializedName("position") val position: Int,
    @SerializedName("displayName") val displayName: String?,
    @SerializedName("implicitConsent") val implicitConsent: Boolean?,
    @SerializedName("href") val href: String,
    @SerializedName("acceptanceState") val acceptedByUser: ApiAcceptedByUser?
)