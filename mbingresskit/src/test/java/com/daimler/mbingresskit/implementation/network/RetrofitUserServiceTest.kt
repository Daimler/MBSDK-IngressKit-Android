package com.daimler.mbingresskit.implementation.network

import com.daimler.mbingresskit.common.ProfileFieldType
import com.daimler.mbingresskit.implementation.mapProfileFieldResponseToProfileField
import com.daimler.mbingresskit.implementation.network.model.profilefields.ProfileFieldTypeResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

internal class RetrofitUserServiceTest {

    @ParameterizedTest
    @EnumSource(ProfileFieldTypeResponse::class)
    fun testMapProfileFieldResponseToProfileField(profileFieldTypeResponse: ProfileFieldTypeResponse) {
        val fieldType = mapProfileFieldResponseToProfileField(profileFieldTypeResponse)
        assertEquals(fieldType, profileFieldTypeResponse.profileFieldType)
    }

    @Test
    fun testMapProfileFieldResponseToProfileField_nullValue() {
        val fieldType = mapProfileFieldResponseToProfileField(null)
        assertEquals(fieldType, ProfileFieldType.UNKNOWN)
    }
}