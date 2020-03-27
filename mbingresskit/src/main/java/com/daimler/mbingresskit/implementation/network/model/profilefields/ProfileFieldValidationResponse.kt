package com.daimler.mbingresskit.implementation.network.model.profilefields

import com.google.gson.annotations.SerializedName

data class ProfileFieldValidationResponse(
    @SerializedName("minLength") val minLength: Int?,
    @SerializedName("maxLength") val maxLength: Int?,
    @SerializedName("regularExpression") val regularExpression: String?
)