package com.daimler.mbingresskit.common

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LdssoUserAgreement(
    override val documentId: String,
    override val originalUrl: String,
    override val documentVersion: String?,
    override val displayName: String?,
    override val locale: String,
    override val countryCode: String,
    override val filePath: String?,
    override val acceptedByUser: AgreementAcceptanceState = AgreementAcceptanceState.UNKNOWN,
    val implicitConsent: Boolean,
    val fileContent: ByteArray?,
    val position: Int,
    override val contentType: UserAgreementContentType = UserAgreementContentType.UNKNOWN,
    override val subsystem: AgreementsSubsystem = AgreementsSubsystem.LDSSO
) : UserAgreement(), Parcelable