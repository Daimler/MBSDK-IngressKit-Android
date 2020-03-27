package com.daimler.mbingresskit.implementation.network.model.agreement

import com.google.gson.annotations.SerializedName

enum class ApiAcceptedByUser {
    @SerializedName("ACCEPTED")
    ACCEPTED,
    @SerializedName("REJECTED")
    REJECTED,
    @SerializedName("UNSEEN_VIA_RISINGSTARS")
    UNSEEN_VIA_RISINGSTARS
}