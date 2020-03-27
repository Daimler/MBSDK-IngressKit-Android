package com.daimler.mbingresskit.persistence.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

internal open class RealmNatconUserAgreement : RealmObject() {

    @PrimaryKey
    var termsId: String = ""

    var url: String? = null
    var version: String? = null
    var title: String? = null
    var description: String? = null
    var mandatory: Boolean? = null
    var position: Int? = null
    var text: String? = null
    var locale: String? = null
    var countryCode: String? = null
    var filePath: String? = null
    var acceptedByUser: Int? = null
    var contentType: Int? = null

    companion object {
        const val FIELD_ID = "termsId"
        const val FIELD_LOCALE = "locale"
        const val FIELD_COUNTRY_CODE = "countryCode"
    }
}