package com.daimler.mbingresskit.persistence

import com.daimler.mbingresskit.persistence.model.*
import com.daimler.mbingresskit.persistence.model.RealmCiamUserAgreement
import com.daimler.mbingresskit.persistence.model.RealmCommunicationPreferences
import com.daimler.mbingresskit.persistence.model.RealmNatconUserAgreement
import com.daimler.mbingresskit.persistence.model.RealmSoeUserAgreement
import com.daimler.mbingresskit.persistence.model.RealmUserAddress
import com.daimler.mbrslogin.persistence.model.RealmLdssoUserAgreement
import io.realm.annotations.RealmModule

@RealmModule(library = true, classes = [
    RealmCiamUserAgreement::class,
    RealmSoeUserAgreement::class,
    RealmNatconUserAgreement::class,
    RealmCustomUserAgreement::class,
    RealmLdssoUserAgreement::class,
    RealmCommunicationPreferences::class,
    RealmUserAddress::class,
    RealmUserBodyHeight::class,
    RealmUserUnitPreferences::class,
    RealmUser::class,
    RealmCountryLocale::class,
    RealmCountry::class
])
class MBIngressRealmModule