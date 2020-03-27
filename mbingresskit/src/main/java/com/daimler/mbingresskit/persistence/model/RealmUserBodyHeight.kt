package com.daimler.mbingresskit.persistence.model

import io.realm.RealmObject

internal open class RealmUserBodyHeight : RealmObject() {

    var bodyHeight: Int? = null
    var preAdjustment: Boolean? = null
}