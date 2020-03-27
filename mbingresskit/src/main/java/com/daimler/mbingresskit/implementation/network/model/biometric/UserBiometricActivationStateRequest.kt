package com.daimler.mbingresskit.implementation.network.model.biometric

import com.google.gson.annotations.SerializedName

data class UserBiometricActivationStateRequest(
    @SerializedName("pin") val pin: String,
    @SerializedName("action") val action: UserBiometricApiState
)