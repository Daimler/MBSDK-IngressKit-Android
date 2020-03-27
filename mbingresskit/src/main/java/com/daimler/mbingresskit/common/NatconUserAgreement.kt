package com.daimler.mbingresskit.common

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NatconUserAgreement(
    override val documentId: String,
    override val originalUrl: String,
    override val documentVersion: String?,
    override val displayName: String?,
    override val locale: String,
    override val countryCode: String,
    override val filePath: String?,
    override val acceptedByUser: AgreementAcceptanceState = AgreementAcceptanceState.UNKNOWN,
    val description: String,
    val text: String,
    val mandatory: Boolean,
    val position: Int,
    val pdfContent: ByteArray?,
    override val contentType: UserAgreementContentType = UserAgreementContentType.UNKNOWN,
    override val subsystem: AgreementsSubsystem = AgreementsSubsystem.NATCON
) : UserAgreement(), Parcelable