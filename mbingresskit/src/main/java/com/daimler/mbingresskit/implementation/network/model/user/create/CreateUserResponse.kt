package com.daimler.mbingresskit.implementation.network.model.user.create

import com.daimler.mbingresskit.implementation.network.model.user.UserCommunicationPreference
import com.google.gson.annotations.SerializedName

data class CreateUserResponse(
    @SerializedName("email") val email: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String?,
    @SerializedName("id") val userId: String,
    @SerializedName("mobileNumber") val phone: String,
    @SerializedName("countryCode") val countryCode: String?,
    @SerializedName("communicationPreference") val communicationPreference: UserCommunicationPreference?
)