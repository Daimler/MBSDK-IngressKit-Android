package com.daimler.mbingresskit.implementation.filestorage

import android.content.Context
import com.daimler.mbingresskit.common.AgreementsSubsystem
import com.daimler.mbingresskit.common.LdssoUserAgreement
import com.daimler.mbingresskit.common.UserAgreementContentType
import com.daimler.mbingresskit.filestorage.FileWriter
import com.daimler.mbloggerkit.MBLoggerKit
import java.io.File

internal class LdssoAgreementsFileStorage(
    context: Context,
    private val fileWriter: FileWriter<ByteArray>
) : BaseAgreementsFileStorage<LdssoUserAgreement, ByteArray>(context) {

    override fun writeFile(data: LdssoUserAgreement, outFile: File): String? {
        return data.fileContent?.let {
            fileWriter.writeToFile(it, outFile)
        } ?: {
            MBLoggerKit.e("No pdf content found.")
            null
        }()
    }

    override fun readFile(inFile: File): ByteArray? {
        return fileWriter.readFile(inFile)
    }

    override fun deleteFiles(): Boolean {
        return deleteAllOfSubsystem(AgreementsSubsystem.LDSSO)
    }

    override fun getFileExtension(agreement: LdssoUserAgreement): AgreementsFileExtension {
        return if (agreement.contentType == UserAgreementContentType.PDF) {
            AgreementsFileExtension.PDF
        } else {
            AgreementsFileExtension.HTML
        }
    }
}