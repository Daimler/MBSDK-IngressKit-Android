package com.daimler.mbingresskit.implementation.network.model

import com.google.gson.annotations.SerializedName

internal data class ApiInputError(
    @SerializedName("fieldName") val fieldName: String?,
    @SerializedName("description") val description: String?
)