package com.daimler.mbingresskit.implementation.network.model.profilefields

import com.google.gson.annotations.SerializedName

data class ProfileFieldsDataResponse(
    @SerializedName("customerDataFields") val customerDataFields: List<CustomerDataFieldResponse>?,
    @SerializedName("fieldDependencies") val fieldDependencies: List<FieldDependencyResponse>?,
    @SerializedName("groupDependencies") val groupDependencies: List<GroupDependencyResponse>?
)