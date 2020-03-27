package com.daimler.mbingresskit.common

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CiamUserAgreement(
    override val documentId: String,
    override val originalUrl: String,
    override val documentVersion: String?,
    override val displayName: String?,
    override val locale: String,
    override val countryCode: String,
    val htmlContent: String?,
    override val filePath: String?,
    override val acceptedByUser: AgreementAcceptanceState = AgreementAcceptanceState.UNKNOWN,
    override val subsystem: AgreementsSubsystem = AgreementsSubsystem.CIAM,
    override val contentType: UserAgreementContentType = UserAgreementContentType.HTML
) : UserAgreement(), Parcelable