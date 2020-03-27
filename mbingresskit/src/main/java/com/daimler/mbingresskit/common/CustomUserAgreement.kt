package com.daimler.mbingresskit.common

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CustomUserAgreement(
    override val documentId: String,
    override val originalUrl: String,
    override val documentVersion: String?,
    override val displayName: String?,
    override val locale: String,
    override val countryCode: String,
    override val filePath: String?,
    override val acceptedByUser: AgreementAcceptanceState = AgreementAcceptanceState.UNKNOWN,
    val appId: String,
    val displayOrder: Int,
    val category: String?,
    val displayLocation: String?,
    val implicitConsent: Boolean,
    val fileContent: ByteArray?,
    override val contentType: UserAgreementContentType = UserAgreementContentType.UNKNOWN,
    override val subsystem: AgreementsSubsystem = AgreementsSubsystem.CUSTOM
) : UserAgreement(), Parcelable