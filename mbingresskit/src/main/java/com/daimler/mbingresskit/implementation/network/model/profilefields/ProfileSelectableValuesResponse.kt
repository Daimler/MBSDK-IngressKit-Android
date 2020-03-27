package com.daimler.mbingresskit.implementation.network.model.profilefields

import com.google.gson.annotations.SerializedName

data class ProfileSelectableValuesResponse(
    @SerializedName("matchSelectableValueByKey") val matchSelectableValueByKey: Boolean,
    @SerializedName("defaultSelectableValueKey") val defaultSelectableValueKey: String?,
    @SerializedName("selectableValues") val selectableValues: List<ProfileSelectableValueResponse>?
)