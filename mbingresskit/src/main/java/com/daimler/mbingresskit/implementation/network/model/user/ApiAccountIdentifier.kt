package com.daimler.mbingresskit.implementation.network.model.user

import com.google.gson.annotations.SerializedName

enum class ApiAccountIdentifier {
    @SerializedName("EMAIL")
    EMAIL,
    @SerializedName("MOBILE")
    MOBILE,
    @SerializedName("EMAIL_AND_MOBILE")
    EMAIL_AND_MOBILE
}