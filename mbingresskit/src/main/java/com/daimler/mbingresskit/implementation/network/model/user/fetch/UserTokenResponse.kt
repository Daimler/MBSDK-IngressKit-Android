package com.daimler.mbingresskit.implementation.network.model.user.fetch

import com.daimler.mbingresskit.implementation.network.model.adaptionvalues.ApiUserAdaptionValues
import com.daimler.mbingresskit.implementation.network.model.unitpreferences.UserUnitPreferences
import com.daimler.mbingresskit.implementation.network.model.user.ApiAccountIdentifier
import com.daimler.mbingresskit.implementation.network.model.user.UserCommunicationPreference
import com.google.gson.annotations.SerializedName

data class UserTokenResponse(
    @SerializedName("ciamId") val ciamId: String,
    @SerializedName("userId") val userId: String?,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName1") val lastName1: String,
    @SerializedName("lastName2") val lastName2: String,
    @SerializedName("birthday") val birthday: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("mobilePhoneNumber") val mobilePhone: String?,
    @SerializedName("landlinePhone") val landlinePhone: String?,
    @SerializedName("accountCountryCode") val accountCountryCode: String?,
    @SerializedName("preferredLanguageCode") val preferredLanguageCode: String?,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("address") val address: AddressResponse?,
    @SerializedName("userPinStatus") val userPinStatus: UserPinStatusResponse?,
    @SerializedName("communicationPreference") val communicationPreference: UserCommunicationPreference?,
    @SerializedName("unitPreferences") val unitPreferences: UserUnitPreferences?,
    @SerializedName("accountIdentifier") val accountIdentifier: ApiAccountIdentifier?,
    @SerializedName("title") val title: String?,
    @SerializedName("salutationCode") val salutationCode: String?,
    @SerializedName("taxNumber") val taxNumber: String?,
    @SerializedName("adaptionValues") val userAdaptionValues: ApiUserAdaptionValues?,
    @SerializedName("accountVerified") val accountVerified: Boolean
) {

    data class AddressResponse(
        @SerializedName("countryCode") val countryCode: String?,
        @SerializedName("state") val state: String?,
        @SerializedName("province") val province: String?,
        @SerializedName("street") val street: String?,
        @SerializedName("houseNo") val houseNumber: String?,
        @SerializedName("zipCode") val zipCode: String?,
        @SerializedName("city") val city: String?,
        @SerializedName("streetType") val streetType: String?,
        @SerializedName("houseName") val houseName: String?,
        @SerializedName("floorNo") val floorNumber: String?,
        @SerializedName("doorNo") val doorNumber: String?,
        @SerializedName("addressLine1") val addressLine1: String?,
        @SerializedName("addressLine2") val addressLine2: String?,
        @SerializedName("addressLine3") val addressLine3: String?,
        @SerializedName("postOfficeBox") val postOfficeBox: String?
    )

    enum class UserPinStatusResponse {
        SET,
        NOT_SET,
        UNKNOWN
    }
}