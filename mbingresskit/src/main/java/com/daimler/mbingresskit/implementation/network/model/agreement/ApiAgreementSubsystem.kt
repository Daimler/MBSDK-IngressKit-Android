package com.daimler.mbingresskit.implementation.network.model.agreement

import com.google.gson.annotations.SerializedName

enum class ApiAgreementSubsystem {
    @SerializedName("CIAM") CIAM,
    @SerializedName("SOE") SOE,
    @SerializedName("NATCON") NATCON,
    @SerializedName("CUSTOM") CUSTOM,
    @SerializedName("LDSSO") LDSSO
}