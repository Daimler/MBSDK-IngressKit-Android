package com.daimler.mbingresskit.persistence.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

internal open class RealmCustomUserAgreement : RealmObject() {

    @PrimaryKey
    var documentId: String = ""

    var url: String? = null
    var documentVersion: String? = null
    var displayName: String? = null
    var locale: String? = null
    var countryCode: String? = null
    var contentType: Int? = null
    var filePath: String? = null
    var acceptedByUser: Int? = null
    var appId: String? = null
    var displayOrder: Int? = null
    var category: String? = null
    var displayLocation: String? = null
    var implicitConsent: Boolean? = null

    companion object {
        const val FIELD_ID = "documentId"
        const val FIELD_LOCALE = "locale"
        const val FIELD_COUNTRY_CODE = "countryCode"
    }
}