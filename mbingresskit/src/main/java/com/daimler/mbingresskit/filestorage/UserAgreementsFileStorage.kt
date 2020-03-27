package com.daimler.mbingresskit.filestorage

import com.daimler.mbingresskit.common.*

/**
 * File storage for user agreements.
 */
internal interface UserAgreementsFileStorage {

    /**
     * Returns a file storage that can take [CiamUserAgreement] as input and reads a string out
     * of a file.
     */
    fun ciamAgreementsStorage(): FileStorage<CiamUserAgreement, String?>

    /**
     * Returns a file storage that can take [SoeUserAgreement] as input and reads a byte array
     * of a file.
     */
    fun soeAgreementsStorage(): FileStorage<SoeUserAgreement, ByteArray?>

    /**
     * Returns a file storage that can take [NatconUserAgreement] as input and reads a byte array
     * of a file.
     */
    fun natconAgreementsStorage(): FileStorage<NatconUserAgreement, ByteArray?>

    /**
     * Returns a file storage that can take [CustomUserAgreement] as input and reads a byte array
     * of a file.
     */
    fun customAgreementsStorage(): FileStorage<CustomUserAgreement, ByteArray?>

    /**
     * Returns a file storage that can take [LdssoUserAgreement] as input and reads a byte array
     * of a file.
     */
    fun ldssoAgreementsStorage(): FileStorage<LdssoUserAgreement, ByteArray?>

    /**
     * Deletes the contents of the storages. More specifically, it calls [FileStorage.deleteFiles]
     * on the storage returned by [ciamAgreementsStorage], [soeAgreementsStorage],
     * [natconAgreementsStorage] and [customAgreementsStorage].
     */
    fun clearAll()
}