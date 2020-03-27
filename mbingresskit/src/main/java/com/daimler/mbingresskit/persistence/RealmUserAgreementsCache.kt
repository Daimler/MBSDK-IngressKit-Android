package com.daimler.mbingresskit.persistence

import com.daimler.mbrslogin.persistence.model.RealmLdssoUserAgreement
import com.daimler.mbloggerkit.MBLoggerKit
import com.daimler.mbingresskit.common.*
import com.daimler.mbingresskit.filestorage.UserAgreementsFileStorage
import com.daimler.mbingresskit.persistence.model.RealmCiamUserAgreement
import com.daimler.mbingresskit.persistence.model.RealmCustomUserAgreement
import com.daimler.mbingresskit.persistence.model.RealmNatconUserAgreement
import com.daimler.mbingresskit.persistence.model.RealmSoeUserAgreement
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where

internal class RealmUserAgreementsCache(
    private val realm: Realm,
    private val agreementsFileStorage: UserAgreementsFileStorage
) : UserAgreementsCache {

    override fun writeCiamAgreement(userAgreement: CiamUserAgreement): CiamUserAgreement? {
        val updated = writeToCiamStorage(userAgreement)
        return updated?.let {
            cacheUserAgreement(it)
            it
        } ?: {
            MBLoggerKit.e(SKIPPED_CACHING)
            null
        }()
    }

    override fun readCiamAgreements(locale: String, countryCode: String): UserAgreements<CiamUserAgreement>? {
        val agreements = ciamForLocale(locale, countryCode)
        return if (agreements.isNotEmpty()) {
            val list = agreements.map { realmAgreement ->
                val agreement = mapRealmAgreementToCiamAgreement(realmAgreement)
                fetchCiamFromStorage(agreement)
            }
            UserAgreements(locale, countryCode, list)
        } else {
            null
        }
    }

    override fun queryCiamAgreements(locale: String, countryCode: String) =
            readCiamAgreements(locale, countryCode)

    override fun updateCiamAcceptance(documentId: String, accepted: Boolean) {
        ciamById(documentId)?.let { agreement ->
            realm.executeTransaction {
                agreement.acceptedByUser = if (accepted) {
                    AgreementAcceptanceState.ACCEPTED.ordinal
                } else {
                    AgreementAcceptanceState.REJECTED.ordinal
                }
                it.copyToRealmOrUpdate(agreement)
            }
        }
    }

    override fun writeSoeAgreement(userAgreement: SoeUserAgreement): SoeUserAgreement? {
        val updated = writeToSoeStorage(userAgreement)
        return updated?.let {
            cacheUserAgreement(it)
            it
        } ?: {
            MBLoggerKit.e(SKIPPED_CACHING)
            null
        }()
    }

    override fun querySoeAgreements(locale: String, countryCode: String): UserAgreements<SoeUserAgreement>? {
        val agreements = soeForLocale(locale, countryCode)
        return if (agreements.isNotEmpty()) {
            val list = agreements.map {
                mapRealmAgreementToSoeAgreement(it)
            }
            UserAgreements(locale, countryCode, list)
        } else {
            null
        }
    }

    override fun readSoeAgreements(locale: String, countryCode: String): UserAgreements<SoeUserAgreement>? {
        val agreements = soeForLocale(locale, countryCode)
        return if (agreements.isNotEmpty()) {
            val list = agreements.map { realmAgreement ->
                val agreement = mapRealmAgreementToSoeAgreement(realmAgreement)
                fetchSoeFromStorage(agreement)
            }
            UserAgreements(locale, countryCode, list)
        } else {
            null
        }
    }

    override fun updateSoeAcceptance(documentId: String, accepted: Boolean) {
        soeById(documentId)?.let { agreement ->
            realm.executeTransaction {
                agreement.acceptedByUser = if (accepted) {
                    AgreementAcceptanceState.ACCEPTED.ordinal
                } else {
                    AgreementAcceptanceState.REJECTED.ordinal
                }
                it.copyToRealmOrUpdate(agreement)
            }
        }
    }

    override fun writeNatconAgreement(userAgreement: NatconUserAgreement): NatconUserAgreement? {
        val updated =
                if (userAgreement.hasPdf()) {
                    writeToNatconStorage(userAgreement)
                } else {
                    userAgreement
                }
        return updated?.let {
            cacheUserAgreement(it)
            it
        } ?: {
            MBLoggerKit.e(SKIPPED_CACHING)
            null
        }()
    }

    override fun readNatconAgreements(locale: String, countryCode: String): UserAgreements<NatconUserAgreement>? {
        val agreements = natconForLocale(locale, countryCode)
        return if (agreements.isNotEmpty()) {
            val list = agreements.map {
                val agreement = mapRealmAgreementToNatconAgreement(it)
                if (agreement.hasPdf()) fetchNatconFromStorage(agreement) else agreement
            }
            UserAgreements(locale, countryCode, list)
        } else {
            null
        }
    }

    override fun queryNatconAgreements(locale: String, countryCode: String): UserAgreements<NatconUserAgreement>? {
        val agreements = natconForLocale(locale, countryCode)
        return if (agreements.isNotEmpty()) {
            val list = agreements.map {
                mapRealmAgreementToNatconAgreement(it)
            }
            UserAgreements(locale, countryCode, list)
        } else {
            null
        }
    }

    override fun updateNatconAcceptance(documentId: String, accepted: Boolean) {
        natconById(documentId)?.let { agreement ->
            realm.executeTransaction {
                agreement.acceptedByUser = if (accepted) {
                    AgreementAcceptanceState.ACCEPTED.ordinal
                } else {
                    AgreementAcceptanceState.REJECTED.ordinal
                }
                it.copyToRealmOrUpdate(agreement)
            }
        }
    }

    override fun writeCustomAgreement(userAgreement: CustomUserAgreement): CustomUserAgreement? {
        val updated = writeToCustomStorage(userAgreement)
        return updated?.let {
            cacheUserAgreement(it)
            it
        } ?: {
            MBLoggerKit.e(SKIPPED_CACHING)
            null
        }()
    }

    override fun readCustomAgreements(locale: String, countryCode: String): UserAgreements<CustomUserAgreement>? {
        val agreements = customForLocale(locale, countryCode)
        return if (agreements.isNotEmpty()) {
            val list = agreements.map { realmAgreement ->
                val agreement = mapRealmAgreementToCustomAgreement(realmAgreement)
                fetchCustomFromStorage(agreement)
            }
            UserAgreements(locale, countryCode, list)
        } else {
            null
        }
    }

    override fun queryCustomAgreements(locale: String, countryCode: String): UserAgreements<CustomUserAgreement>? {
        val agreements = customForLocale(locale, countryCode)
        return if (agreements.isNotEmpty()) {
            val list = agreements.map {
                mapRealmAgreementToCustomAgreement(it)
            }
            UserAgreements(locale, countryCode, list)
        } else {
            null
        }
    }

    override fun updateCustomAcceptance(documentId: String, accepted: Boolean) {
        customById(documentId)?.let { agreement ->
            realm.executeTransaction {
                agreement.acceptedByUser = if (accepted) {
                    AgreementAcceptanceState.ACCEPTED.ordinal
                } else {
                    AgreementAcceptanceState.REJECTED.ordinal
                }
                it.copyToRealmOrUpdate(agreement)
            }
        }
    }

    override fun readLdssoAgreements(locale: String, countryCode: String): UserAgreements<LdssoUserAgreement>? {
        val agreements = ldssoForLocale(locale, countryCode)
        return if (agreements.isNotEmpty()) {
            val list = agreements.map { realmAgreement ->
                val agreement = mapRealmAgreementToLdssoAgreement(realmAgreement)
                fetchLdssoFromStorage(agreement)
            }
            UserAgreements(locale, countryCode, list)
        } else {
            null
        }
    }

    override fun writeLdssoAgreement(userAgreement: LdssoUserAgreement): LdssoUserAgreement? {
        val updated = writeToLdssoStorage(userAgreement)
        return updated?.let {
            cacheUserAgreement(it)
            it
        } ?: {
            MBLoggerKit.e(SKIPPED_CACHING)
            null
        }()
    }

    override fun queryLdssoAgreements(locale: String, countryCode: String): UserAgreements<LdssoUserAgreement>? {
        val agreements = ldssoForLocale(locale, countryCode)
        return if (agreements.isNotEmpty()) {
            val list = agreements.map {
                mapRealmAgreementToLdssoAgreement(it)
            }
            UserAgreements(locale, countryCode, list)
        } else {
            null
        }
    }

    override fun updateLdssoAcceptance(documentId: String, accepted: Boolean) {
        ldssoById(documentId)?.let { agreement ->
            realm.executeTransaction {
                agreement.acceptedByUser = if (accepted) {
                    AgreementAcceptanceState.ACCEPTED.ordinal
                } else {
                    AgreementAcceptanceState.REJECTED.ordinal
                }
                it.copyToRealmOrUpdate(agreement)
            }
        }
    }

    override fun clear() {
        realm.executeTransaction {
            realm.where<RealmCiamUserAgreement>()
                    .findAll()
                    .deleteAllFromRealm()
            realm.where<RealmSoeUserAgreement>()
                    .findAll()
                    .deleteAllFromRealm()
            realm.where<RealmNatconUserAgreement>()
                    .findAll()
                    .deleteAllFromRealm()
            realm.where<RealmCustomUserAgreement>()
                    .findAll()
                    .deleteAllFromRealm()
            realm.where<RealmLdssoUserAgreement>()
                .findAll()
                .deleteAllFromRealm()
        }
        agreementsFileStorage.clearAll()
    }

    private fun cacheUserAgreement(userAgreement: SoeUserAgreement) {
        val id = userAgreement.documentId
        var current = soeById(id)
        realm.executeTransaction {
            if (current == null) {
                MBLoggerKit.d("Creating agreement object in db for agreement $id.")
                current = realm.createObject(id)
            } else {
                MBLoggerKit.d("Updating agreement $id.")
            }
            current?.apply {
                url = userAgreement.originalUrl
                documentVersion = userAgreement.documentVersion
                displayOrder = userAgreement.displayOrder
                displayName = userAgreement.displayName
                locale = userAgreement.locale
                countryCode = userAgreement.countryCode
                contentType = userAgreement.contentType.ordinal
                filePath = userAgreement.filePath
                acceptedByUser = userAgreement.acceptedByUser.ordinal
                generalUserAgreement = userAgreement.generalUserAgreement
                checkBoxText = userAgreement.checkBoxText
                titleText = userAgreement.titleText

                it.copyToRealmOrUpdate(this)
            }
        }
    }

    private fun cacheUserAgreement(userAgreement: CiamUserAgreement) {
        val id = userAgreement.documentId
        var current = ciamById(id)
        realm.executeTransaction {
            if (current == null) {
                MBLoggerKit.d("Creating agreement object in db for agreement $id.")
                current = realm.createObject(id)
            } else {
                MBLoggerKit.d("Updating agreement $id.")
            }
            current?.apply {
                url = userAgreement.originalUrl
                documentVersion = userAgreement.documentVersion
                displayName = userAgreement.displayName
                locale = userAgreement.locale
                countryCode = userAgreement.countryCode
                contentType = userAgreement.contentType.ordinal
                filePath = userAgreement.filePath
                acceptedByUser = userAgreement.acceptedByUser.ordinal

                it.copyToRealmOrUpdate(this)
            }
        }
    }

    private fun cacheUserAgreement(userAgreement: NatconUserAgreement) {
        val id = userAgreement.documentId
        var current = natconById(id)
        realm.executeTransaction {
            if (current == null) {
                MBLoggerKit.d("Creating agreement object in db for agreement $id.")
                current = realm.createObject(id)
            } else {
                MBLoggerKit.d("Updating agreement $id.")
            }
            current?.apply {
                url = userAgreement.originalUrl
                version = userAgreement.documentVersion
                title = userAgreement.displayName
                description = userAgreement.description
                mandatory = userAgreement.mandatory
                position = userAgreement.position
                text = userAgreement.text
                locale = userAgreement.locale
                countryCode = userAgreement.countryCode
                filePath = userAgreement.filePath
                acceptedByUser = userAgreement.acceptedByUser.ordinal
                contentType = userAgreement.contentType.ordinal

                it.copyToRealmOrUpdate(this)
            }
        }
    }

    private fun cacheUserAgreement(userAgreement: CustomUserAgreement) {
        val id = userAgreement.documentId
        var current = customById(id)
        realm.executeTransaction {
            if (current == null) {
                MBLoggerKit.d("Creating agreement object in db for agreement: $id.")
                current = realm.createObject(id)
            } else {
                MBLoggerKit.d("Updating agreement $id.")
            }
            current?.apply {
                url = userAgreement.originalUrl
                documentVersion = userAgreement.documentVersion
                displayName = userAgreement.displayName
                locale = userAgreement.locale
                countryCode = userAgreement.countryCode
                contentType = userAgreement.contentType.ordinal
                filePath = userAgreement.filePath
                acceptedByUser = userAgreement.acceptedByUser.ordinal
                appId = userAgreement.appId
                displayOrder = userAgreement.displayOrder
                category = userAgreement.category
                displayLocation = userAgreement.displayLocation
                implicitConsent = userAgreement.implicitConsent

                it.copyToRealmOrUpdate(this)
            }
        }
    }

    private fun cacheUserAgreement(userAgreement: LdssoUserAgreement) {
        val id = userAgreement.documentId
        var current = ldssoById(id)
        realm.executeTransaction {
            if (current == null) {
                MBLoggerKit.d("Creating agreement object in db for agreement: $id.")
                current = realm.createObject(id)
            } else {
                MBLoggerKit.d("Updating agreement $id.")
            }
            current?.apply {
                url = userAgreement.originalUrl
                documentVersion = userAgreement.documentVersion
                displayName = userAgreement.displayName
                position = userAgreement.position
                locale = userAgreement.locale
                countryCode = userAgreement.countryCode
                contentType = userAgreement.contentType.ordinal
                filePath = userAgreement.filePath
                acceptedByUser = userAgreement.acceptedByUser.ordinal
                implicitConsent = userAgreement.implicitConsent

                it.copyToRealmOrUpdate(this)
            }
        }
    }

    private fun fetchCiamFromStorage(userAgreement: CiamUserAgreement): CiamUserAgreement {
        val storageResult = agreementsFileStorage.ciamAgreementsStorage().readFromFile(userAgreement)
        return storageResult?.let {
            userAgreement.copy(htmlContent = it)
        } ?: userAgreement
    }

    private fun writeToCiamStorage(userAgreement: CiamUserAgreement): CiamUserAgreement? {
        val result = agreementsFileStorage.ciamAgreementsStorage().writeToFile(userAgreement)
        return result?.let { userAgreement.copy(filePath = it) }
    }

    private fun fetchSoeFromStorage(userAgreement: SoeUserAgreement): SoeUserAgreement {
        val storageResult = agreementsFileStorage.soeAgreementsStorage().readFromFile(userAgreement)
        return storageResult?.let {
            userAgreement.copy(pdfContent = it)
        } ?: userAgreement
    }

    private fun writeToSoeStorage(userAgreement: SoeUserAgreement): SoeUserAgreement? {
        val result = agreementsFileStorage.soeAgreementsStorage().writeToFile(userAgreement)
        return result?.let { userAgreement.copy(filePath = it) }
    }

    private fun fetchNatconFromStorage(userAgreement: NatconUserAgreement): NatconUserAgreement {
        val storageResult = agreementsFileStorage.natconAgreementsStorage().readFromFile(userAgreement)
        return storageResult?.let {
            userAgreement.copy(pdfContent = it)
        } ?: userAgreement
    }

    private fun writeToNatconStorage(userAgreement: NatconUserAgreement): NatconUserAgreement? {
        val result = agreementsFileStorage.natconAgreementsStorage().writeToFile(userAgreement)
        return result?.let { userAgreement.copy(filePath = it) }
    }

    private fun fetchCustomFromStorage(userAgreement: CustomUserAgreement): CustomUserAgreement {
        val storageResult = agreementsFileStorage.customAgreementsStorage().readFromFile(userAgreement)
        return storageResult?.let {
            userAgreement.copy(fileContent = it)
        } ?: userAgreement
    }

    private fun fetchLdssoFromStorage(userAgreement: LdssoUserAgreement): LdssoUserAgreement {
        val storageResult = agreementsFileStorage.ldssoAgreementsStorage().readFromFile(userAgreement)
        return storageResult?.let {
            userAgreement.copy(fileContent = it)
        } ?: userAgreement
    }

    private fun writeToCustomStorage(userAgreement: CustomUserAgreement): CustomUserAgreement? {
        val result = agreementsFileStorage.customAgreementsStorage().writeToFile(userAgreement)
        return result?.let { userAgreement.copy(filePath = it) }
    }

    private fun writeToLdssoStorage(userAgreement: LdssoUserAgreement): LdssoUserAgreement? {
        val result = agreementsFileStorage.ldssoAgreementsStorage().writeToFile(userAgreement)
        return result?.let { userAgreement.copy(filePath = it) }
    }

    private fun ciamById(id: String) =
            realm.where<RealmCiamUserAgreement>()
                    .equalTo(RealmCiamUserAgreement.FIELD_ID, id)
                    .findFirst()

    private fun soeById(id: String) =
            realm.where<RealmSoeUserAgreement>()
                    .equalTo(RealmSoeUserAgreement.FIELD_ID, id)
                    .findFirst()

    private fun natconById(id: String) =
            realm.where<RealmNatconUserAgreement>()
                    .equalTo(RealmNatconUserAgreement.FIELD_ID, id)
                    .findFirst()

    private fun customById(id: String) =
            realm.where<RealmCustomUserAgreement>()
                    .equalTo(RealmCustomUserAgreement.FIELD_ID, id)
                    .findFirst()

    private fun ldssoById(id: String) =
        realm.where<RealmLdssoUserAgreement>()
            .equalTo(RealmLdssoUserAgreement.FIELD_ID, id)
            .findFirst()

    private fun ciamForLocale(
        locale: String,
        countryCode: String
    ) =
            realm.where<RealmCiamUserAgreement>()
                    .equalTo(RealmCiamUserAgreement.FIELD_LOCALE, locale)
                    .and()
                    .equalTo(RealmCiamUserAgreement.FIELD_COUNTRY_CODE, countryCode)
                    .findAll()

    private fun soeForLocale(
        locale: String,
        countryCode: String
    ) =
            realm.where<RealmSoeUserAgreement>()
                    .equalTo(RealmSoeUserAgreement.FIELD_LOCALE, locale)
                    .and()
                    .equalTo(RealmSoeUserAgreement.FIELD_COUNTRY_CODE, countryCode)
                    .findAll()

    private fun natconForLocale(
        locale: String,
        countryCode: String
    ) =
            realm.where<RealmNatconUserAgreement>()
                    .equalTo(RealmNatconUserAgreement.FIELD_LOCALE, locale)
                    .and()
                    .equalTo(RealmNatconUserAgreement.FIELD_COUNTRY_CODE, countryCode)
                    .findAll()

    private fun customForLocale(
        locale: String,
        countryCode: String
    ) =
            realm.where<RealmCustomUserAgreement>()
                    .equalTo(RealmCustomUserAgreement.FIELD_LOCALE, locale)
                    .and()
                    .equalTo(RealmCustomUserAgreement.FIELD_COUNTRY_CODE, countryCode)
                    .findAll()

    private fun ldssoForLocale(
        locale: String,
        countryCode: String
    ) =
            realm.where<RealmLdssoUserAgreement>()
                .equalTo(RealmLdssoUserAgreement.FIELD_LOCALE, locale)
                .and()
                .equalTo(RealmLdssoUserAgreement.FIELD_COUNTRY_CODE, countryCode)
                .findAll()

    private fun mapRealmAgreementToCiamAgreement(realmUserAgreement: RealmCiamUserAgreement) =
            CiamUserAgreement(
                    realmUserAgreement.documentId,
                    realmUserAgreement.url.orEmpty(),
                    realmUserAgreement.documentVersion,
                    realmUserAgreement.displayName,
                    realmUserAgreement.locale.orEmpty(),
                    realmUserAgreement.countryCode.orEmpty(),
                    null,
                    realmUserAgreement.filePath,
                    UserAgreement.acceptanceStateFromInt(realmUserAgreement.acceptedByUser ?: -1)
            )

    private fun mapRealmAgreementToSoeAgreement(realmUserAgreement: RealmSoeUserAgreement) =
            SoeUserAgreement(
                    realmUserAgreement.documentId,
                    realmUserAgreement.url.orEmpty(),
                    realmUserAgreement.documentVersion,
                    realmUserAgreement.displayOrder ?: -1,
                    realmUserAgreement.displayName,
                    realmUserAgreement.locale.orEmpty(),
                    realmUserAgreement.countryCode.orEmpty(),
                    null,
                    realmUserAgreement.filePath,
                    UserAgreement.acceptanceStateFromInt(realmUserAgreement.acceptedByUser ?: -1),
                    realmUserAgreement.generalUserAgreement == true,
                    realmUserAgreement.checkBoxText,
                    realmUserAgreement.titleText
            )

    private fun mapRealmAgreementToNatconAgreement(realmUserAgreement: RealmNatconUserAgreement) =
            NatconUserAgreement(
                    realmUserAgreement.termsId,
                    realmUserAgreement.url.orEmpty(),
                    realmUserAgreement.version,
                    realmUserAgreement.title,
                    realmUserAgreement.locale.orEmpty(),
                    realmUserAgreement.countryCode.orEmpty(),
                    realmUserAgreement.filePath,
                    UserAgreement.acceptanceStateFromInt(realmUserAgreement.acceptedByUser ?: -1),
                    realmUserAgreement.description.orEmpty(),
                    realmUserAgreement.text.orEmpty(),
                    realmUserAgreement.mandatory == true,
                    realmUserAgreement.position ?: -1,
                    null,
                    UserAgreement.contentTypeFromInt(realmUserAgreement.contentType ?: -1)
            )

    private fun mapRealmAgreementToCustomAgreement(realmUserAgreement: RealmCustomUserAgreement) =
            CustomUserAgreement(
                    realmUserAgreement.documentId,
                    realmUserAgreement.url.orEmpty(),
                    realmUserAgreement.documentVersion,
                    realmUserAgreement.displayName,
                    realmUserAgreement.locale.orEmpty(),
                    realmUserAgreement.countryCode.orEmpty(),
                    realmUserAgreement.filePath,
                    UserAgreement.acceptanceStateFromInt(realmUserAgreement.acceptedByUser ?: -1),
                    realmUserAgreement.appId.orEmpty(),
                    realmUserAgreement.displayOrder ?: -1,
                    realmUserAgreement.category,
                    realmUserAgreement.displayLocation,
                    realmUserAgreement.implicitConsent == true,
                    null,
                    UserAgreement.contentTypeFromInt(realmUserAgreement.contentType ?: -1)
            )

    private fun mapRealmAgreementToLdssoAgreement(realmUserAgreement: RealmLdssoUserAgreement) =
        LdssoUserAgreement(
            realmUserAgreement.documentId,
            realmUserAgreement.url.orEmpty(),
            realmUserAgreement.documentVersion,
            realmUserAgreement.displayName,
            realmUserAgreement.locale.orEmpty(),
            realmUserAgreement.countryCode.orEmpty(),
            realmUserAgreement.filePath,
            UserAgreement.acceptanceStateFromInt(realmUserAgreement.acceptedByUser ?: -1),
            realmUserAgreement.implicitConsent == true,
            null,
            realmUserAgreement.position ?: -1,
            UserAgreement.contentTypeFromInt(realmUserAgreement.contentType ?: -1)
        )

    private fun UserAgreement.hasPdf() = contentType == UserAgreementContentType.PDF

    companion object {
        private const val SKIPPED_CACHING = "Skipped file caching because file could not be written."
    }
}