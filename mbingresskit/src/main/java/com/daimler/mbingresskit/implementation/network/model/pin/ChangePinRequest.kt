package com.daimler.mbingresskit.implementation.network.model.pin

import com.google.gson.annotations.SerializedName

data class ChangePinRequest(
    @SerializedName("currentPin") val currentPin: String,
    @SerializedName("newPin") val newPin: String
)