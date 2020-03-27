package com.daimler.mbingresskit.implementation.network.model.user

import com.google.gson.annotations.SerializedName

data class ApiVerifyUserRequest(
    @SerializedName("scanReference") val scanReference: String
)