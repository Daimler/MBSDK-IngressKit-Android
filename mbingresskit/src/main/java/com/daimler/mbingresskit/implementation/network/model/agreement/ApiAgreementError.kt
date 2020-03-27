package com.daimler.mbingresskit.implementation.network.model.agreement

import com.google.gson.annotations.SerializedName

data class ApiAgreementError(
    @SerializedName("Error") val error: String?,
    @SerializedName("Subsystem") val subsystem: ApiAgreementSubsystem?
)