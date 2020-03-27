package com.daimler.mbingresskit.common

abstract class UserAgreement {
    abstract val documentId: String
    abstract val originalUrl: String
    abstract val documentVersion: String?
    abstract val displayName: String?
    abstract val subsystem: AgreementsSubsystem
    abstract val contentType: UserAgreementContentType
    abstract val locale: String
    abstract val countryCode: String
    abstract val acceptedByUser: AgreementAcceptanceState
    abstract val filePath: String?

    companion object {

        fun acceptanceStateFromInt(ordinal: Int) =
                AgreementAcceptanceState.values().getOrElse(ordinal) { AgreementAcceptanceState.UNKNOWN }

        fun contentTypeFromInt(ordinal: Int) =
                UserAgreementContentType.values().getOrElse(ordinal) { UserAgreementContentType.UNKNOWN }
    }
}