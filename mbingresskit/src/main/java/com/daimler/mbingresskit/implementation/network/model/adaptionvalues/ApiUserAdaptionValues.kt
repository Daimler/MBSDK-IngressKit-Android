package com.daimler.mbingresskit.implementation.network.model.adaptionvalues

import com.google.gson.annotations.SerializedName

data class ApiUserAdaptionValues(
    @SerializedName("bodyHeight") val bodyHeight: Int,
    @SerializedName("preAdjustment") val preAdjustment: Boolean
)