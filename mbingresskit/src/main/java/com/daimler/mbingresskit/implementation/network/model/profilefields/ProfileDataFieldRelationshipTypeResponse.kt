package com.daimler.mbingresskit.implementation.network.model.profilefields

import com.google.gson.annotations.SerializedName

enum class ProfileDataFieldRelationshipTypeResponse {
    @SerializedName("GROUP")
    GROUP,
    @SerializedName("DATAFIELD")
    DATA_FIELD
}