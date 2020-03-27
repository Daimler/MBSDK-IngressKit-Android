package com.daimler.mbingresskit.persistence

import com.daimler.mbingresskit.common.*

/**
 * Local cache for user agreements.
 */
interface UserAgreementsCache {

    /**
     * Writes the meta data and file content of the given ToU.
     * Returns a copy of the given [userAgreement] that contains the file path of the cached file.
     * Returns null if the file content could not be written to the file storage.
     */
    fun writeCiamAgreement(userAgreement: CiamUserAgreement): CiamUserAgreement?

    /**
     * Reads the metadata and file contents of the CIAM ToU for the given locale and country.
     */
    fun readCiamAgreements(locale: String, countryCode: String): UserAgreements<CiamUserAgreement>?

    /**
     * Reads the metadata of the CIAM ToU for the given locale and country.
     */
    fun queryCiamAgreements(locale: String, countryCode: String): UserAgreements<CiamUserAgreement>?

    fun updateCiamAcceptance(documentId: String, accepted: Boolean)

    /**
     * Writes the meta data and file content of the given ToU.
     * Returns a copy of the given [userAgreement] that contains the file path of the cached file.
     * Returns null if the file content could not be written to the file storage.
     */
    fun writeSoeAgreement(userAgreement: SoeUserAgreement): SoeUserAgreement?

    /**
     * Updates the acceptance flag of the local cached SOE ToU with the given id.
     */
    fun updateSoeAcceptance(documentId: String, accepted: Boolean)

    /**
     * Reads the metadata and file contents of the SOE ToU for the given locale and country.
     */
    fun readSoeAgreements(locale: String, countryCode: String): UserAgreements<SoeUserAgreement>?

    /**
     * Reads the metadata of the SOE ToU for the given locale and country.
     */
    fun querySoeAgreements(locale: String, countryCode: String): UserAgreements<SoeUserAgreement>?

    /**
     * Writes the meta data and file content of the given ToU.
     * Returns a copy of the given [userAgreement] that contains the file path of the cached file.
     * Returns null if the file content could not be written to the file storage.
     */
    fun writeNatconAgreement(userAgreement: NatconUserAgreement): NatconUserAgreement?

    /**
     * Reads the metadata and file contents of the NATCON ToU for the given locale and country.
     */
    fun readNatconAgreements(locale: String, countryCode: String): UserAgreements<NatconUserAgreement>?

    /**
     * Reads the metadata of the NATCON ToU for the given locale and country.
     */
    fun queryNatconAgreements(locale: String, countryCode: String): UserAgreements<NatconUserAgreement>?

    fun updateNatconAcceptance(documentId: String, accepted: Boolean)

    /**
     * Writes the meta data and file content of the given ToU.
     * Returns a copy of the given [userAgreement] that contains the file path of the cached file.
     * Returns null if the file content could not be written to the file storage.
     */
    fun writeCustomAgreement(userAgreement: CustomUserAgreement): CustomUserAgreement?

    /**
     * Reads the metadata and file contents of the Custom ToU for the given locale and country.
     */
    fun readCustomAgreements(locale: String, countryCode: String): UserAgreements<CustomUserAgreement>?

    /**
     * Reads the metadata of the Custom ToU for the given locale and country.
     */
    fun queryCustomAgreements(locale: String, countryCode: String): UserAgreements<CustomUserAgreement>?

    fun updateCustomAcceptance(documentId: String, accepted: Boolean)

    /**
     * Reads the metadata and file contents of the LDSSO ToU for the given locale and country.
     */
    fun readLdssoAgreements(locale: String, countryCode: String): UserAgreements<LdssoUserAgreement>?

    /**
     * Writes the meta data and file content of the given ToU.
     * Returns a copy of the given [userAgreement] that contains the file path of the cached file.
     * Returns null if the file content could not be written to the file storage.
     */
    fun writeLdssoAgreement(userAgreement: LdssoUserAgreement): LdssoUserAgreement?

    /**
     * Reads the metadata of the LDSSO ToU for the given locale and country.
     */
    fun queryLdssoAgreements(locale: String, countryCode: String): UserAgreements<LdssoUserAgreement>?

    fun updateLdssoAcceptance(documentId: String, accepted: Boolean)

    /**
     * Clears the cache. It will be empty afterwards.
     */
    fun clear()
}