package com.daimler.mbingresskit.implementation.network.model.agreement

import com.google.gson.annotations.SerializedName

data class ApiAgreementsResponse(
    @SerializedName("Errors") val errors: List<ApiAgreementError>?,
    @SerializedName("SOE") val soe: List<ApiSoeAgreement>?,
    @SerializedName("CIAM") val ciam: List<ApiCiamAgreement>?,
    @SerializedName("NATCON") val natcon: List<ApiNatconAgreement>?,
    @SerializedName("CUSTOM") val custom: List<ApiCustomAgreement>?,
    @SerializedName("LDSSO") val ldsso: List<ApiLdssoAgreement>?
)