package com.daimler.mbingresskit.implementation.network.model.user

import com.google.gson.annotations.SerializedName

data class UserCommunicationPreference(
    @SerializedName("contactedByPhone") val contactByPhone: Boolean,
    @SerializedName("contactedByLetter") val contactByLetter: Boolean,
    @SerializedName("contactedByEmail") val contactByMail: Boolean,
    @SerializedName("contactedBySms") val contactBySms: Boolean
)