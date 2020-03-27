package com.daimler.mbingresskit.implementation.network.model.profilefields

import com.google.gson.annotations.SerializedName

data class FieldDependencyResponse(
    @SerializedName("fieldOwnerType") val fieldOwnerType: FieldOwnerTypeResponse?,
    @SerializedName("itemId") val itemId: ProfileFieldTypeResponse?,
    @SerializedName("profileDataFieldRelationshipType") val fieldType: ProfileDataFieldRelationshipTypeResponse?,
    @SerializedName("childrenIds") val childrenIds: List<ProfileFieldTypeResponse?>?
)