package com.daimler.mbingresskit.implementation.network.model.pin

import com.google.gson.annotations.SerializedName

data class LoginUserResponse(
    @SerializedName("username") val userName: String,
    @SerializedName("isEmail") val isEmail: Boolean
)