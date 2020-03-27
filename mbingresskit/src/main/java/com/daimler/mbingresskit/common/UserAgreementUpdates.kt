package com.daimler.mbingresskit.common

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserAgreementUpdates(
    val countryCode: String,
    val updates: List<UserAgreementUpdate>
) : Parcelable