package com.daimler.mbingresskit.implementation

import com.daimler.mbingresskit.common.ProfileFieldType
import com.daimler.mbingresskit.implementation.network.model.profilefields.ProfileFieldTypeResponse

internal fun mapProfileFieldResponseToProfileField(field: ProfileFieldTypeResponse?) =
    field?.profileFieldType ?: ProfileFieldType.UNKNOWN