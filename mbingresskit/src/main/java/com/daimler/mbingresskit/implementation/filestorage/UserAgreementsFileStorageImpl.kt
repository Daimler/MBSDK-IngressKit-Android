package com.daimler.mbingresskit.implementation.filestorage

import android.content.Context
import com.daimler.mbingresskit.common.*
import com.daimler.mbingresskit.filestorage.FileStorage
import com.daimler.mbingresskit.filestorage.UserAgreementsFileStorage

internal class UserAgreementsFileStorageImpl(context: Context) : UserAgreementsFileStorage {

    private val ciamStorage: FileStorage<CiamUserAgreement, String?> by lazy {
        CiamAgreementsFileStorage(context, HtmlFileWriter())
    }

    private val soeStorage: FileStorage<SoeUserAgreement, ByteArray?> by lazy {
        SoeAgreementsFileStorage(context, PdfFileWriter())
    }

    private val natconStorage: FileStorage<NatconUserAgreement, ByteArray?> by lazy {
        NatconAgreementsFileStorage(context, PdfFileWriter())
    }

    private val customStorage: FileStorage<CustomUserAgreement, ByteArray?> by lazy {
        CustomAgreementsFileStorage(context, PdfFileWriter())
    }

    private val ldssoStorage: FileStorage<LdssoUserAgreement, ByteArray?> by lazy {
        LdssoAgreementsFileStorage(context, PdfFileWriter())
    }

    override fun ciamAgreementsStorage(): FileStorage<CiamUserAgreement, String?> {
        return ciamStorage
    }

    override fun soeAgreementsStorage(): FileStorage<SoeUserAgreement, ByteArray?> {
        return soeStorage
    }

    override fun natconAgreementsStorage(): FileStorage<NatconUserAgreement, ByteArray?> {
        return natconStorage
    }

    override fun customAgreementsStorage(): FileStorage<CustomUserAgreement, ByteArray?> {
        return customStorage
    }

    override fun ldssoAgreementsStorage(): FileStorage<LdssoUserAgreement, ByteArray?> {
        return ldssoStorage
    }

    override fun clearAll() {
        ciamStorage.deleteFiles()
        soeStorage.deleteFiles()
        natconStorage.deleteFiles()
        customStorage.deleteFiles()
        ldssoStorage.deleteFiles()
    }
}