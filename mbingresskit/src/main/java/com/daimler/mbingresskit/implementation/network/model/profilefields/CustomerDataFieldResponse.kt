package com.daimler.mbingresskit.implementation.network.model.profilefields

import com.google.gson.annotations.SerializedName

data class CustomerDataFieldResponse(
    @SerializedName("fieldId") val fieldType: ProfileFieldTypeResponse?,
    @SerializedName("sequenceOrder") val sequenceOrder: Int,
    @SerializedName("fieldUsage") val fieldUsageResponse: ProfileFieldUsageResponse?,
    @SerializedName("fieldValidation") val fieldValidation: ProfileFieldValidationResponse?,
    @SerializedName("selectableValues") val selectableValues: ProfileSelectableValuesResponse?
)