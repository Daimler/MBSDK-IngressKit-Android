package com.daimler.mbingresskit.implementation.network.model.agreement

import com.google.gson.annotations.SerializedName

data class ApiUpdateUserAgreements(
    @SerializedName("userAgreements") val userAgreements: List<ApiUpdateUserAgreement>
)