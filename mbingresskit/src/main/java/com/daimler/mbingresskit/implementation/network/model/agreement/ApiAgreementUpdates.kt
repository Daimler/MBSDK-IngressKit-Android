package com.daimler.mbingresskit.implementation.network.model.agreement

import com.google.gson.annotations.SerializedName

data class ApiAgreementUpdates(
    @SerializedName("customLegalTexts") val custom: List<ApiAgreementUpdate>? = null,
    @SerializedName("natconLegalTexts") val natcon: List<ApiAgreementUpdate>? = null,
    @SerializedName("soeLegalTexts") val soe: List<ApiAgreementUpdate>? = null,
    @SerializedName("ciamLegalTexts") val ciam: List<ApiAgreementUpdate>? = null,
    @SerializedName("ldssoLegalTexts") val ldsso: List<ApiAgreementUpdate>? = null
)