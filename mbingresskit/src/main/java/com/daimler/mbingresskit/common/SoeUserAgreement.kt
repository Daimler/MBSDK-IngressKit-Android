package com.daimler.mbingresskit.common

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Suppress("ArrayInDataClass")
@Parcelize
data class SoeUserAgreement(
    override val documentId: String,
    override val originalUrl: String,
    override val documentVersion: String?,
    val displayOrder: Int,
    override val displayName: String?,
    override val locale: String,
    override val countryCode: String,
    val pdfContent: ByteArray?,
    override val filePath: String?,
    override val acceptedByUser: AgreementAcceptanceState = AgreementAcceptanceState.UNKNOWN,
    val generalUserAgreement: Boolean,
    val checkBoxText: String?,
    val titleText: String?,
    override val subsystem: AgreementsSubsystem = AgreementsSubsystem.SOE,
    override val contentType: UserAgreementContentType = UserAgreementContentType.PDF
) : UserAgreement(), Parcelable