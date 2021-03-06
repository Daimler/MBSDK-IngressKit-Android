package com.daimler.mbingresskit.implementation.network.model.user.create

import com.daimler.mbingresskit.implementation.network.model.user.update.UpdateUserRequestAddress
import com.daimler.mbingresskit.implementation.network.model.user.UserCommunicationPreference
import com.google.gson.annotations.SerializedName

data class CreateUserRequest(
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName1") val lastName1: String,
    @SerializedName("birthday") val birthday: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("mobilePhoneNumber") val mobilePhone: String?,
    @SerializedName("landlinePhone") val landlinePhone: String?,
    @SerializedName("accountCountryCode") val accountCountryCode: String,
    @SerializedName("preferredLanguageCode") val preferredLanguageCode: String,
    @SerializedName("address") val address: UpdateUserRequestAddress?,
    @SerializedName("title") val title: String?,
    @SerializedName("salutationCode") val salutationCode: String?,
    @SerializedName("taxNumber") val taxNumber: String?,
    @SerializedName("communicationPreference") val communicationPreference: UserCommunicationPreference?,
    @SerializedName("useEmailAsUsername") val useEmailAsUsername: Boolean
)