package com.daimler.mbingresskit.persistence.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

internal open class RealmSoeUserAgreement : RealmObject() {

    @PrimaryKey
    var documentId: String = ""

    var url: String? = null
    var documentVersion: String? = null
    var displayOrder: Int? = null
    var displayName: String? = null
    var locale: String? = null
    var countryCode: String? = null
    var contentType: Int? = null
    var filePath: String? = null
    var acceptedByUser: Int? = null
    var generalUserAgreement: Boolean? = null
    var checkBoxText: String? = null
    var titleText: String? = null

    companion object {
        const val FIELD_ID = "documentId"
        const val FIELD_LOCALE = "locale"
        const val FIELD_COUNTRY_CODE = "countryCode"
    }
}