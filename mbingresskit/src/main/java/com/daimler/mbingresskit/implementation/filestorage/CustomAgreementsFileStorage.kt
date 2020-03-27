package com.daimler.mbingresskit.implementation.filestorage

import android.content.Context
import com.daimler.mbloggerkit.MBLoggerKit
import com.daimler.mbingresskit.common.AgreementsSubsystem
import com.daimler.mbingresskit.common.CustomUserAgreement
import com.daimler.mbingresskit.common.UserAgreementContentType
import com.daimler.mbingresskit.filestorage.FileWriter
import java.io.File

internal class CustomAgreementsFileStorage(
    context: Context,
    private val fileWriter: FileWriter<ByteArray>
) : BaseAgreementsFileStorage<CustomUserAgreement, ByteArray>(context) {

    override fun writeFile(data: CustomUserAgreement, outFile: File): String? {
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
        return deleteAllOfSubsystem(AgreementsSubsystem.CUSTOM)
    }

    override fun getFileExtension(agreement: CustomUserAgreement): AgreementsFileExtension {
        return if (agreement.contentType == UserAgreementContentType.PDF) {
            AgreementsFileExtension.PDF
        } else {
            AgreementsFileExtension.HTML
        }
    }
}