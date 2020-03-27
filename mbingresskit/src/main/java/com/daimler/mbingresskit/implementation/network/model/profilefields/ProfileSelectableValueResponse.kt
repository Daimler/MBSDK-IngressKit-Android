package com.daimler.mbingresskit.implementation.network.model.profilefields

import com.google.gson.annotations.SerializedName

data class ProfileSelectableValueResponse(
    @SerializedName("key") val key: String,
    @SerializedName("description") val description: String
)