package com.daimler.mbingresskit.implementation.network.model.biometric

import com.google.gson.annotations.SerializedName

enum class UserBiometricApiState {
    @SerializedName("enabled") ENABLED,
    @SerializedName("disabled") DISABLED
}