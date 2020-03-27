package com.daimler.mbingresskit.implementation.filestorage

import android.content.Context
import com.daimler.mbloggerkit.MBLoggerKit
import com.daimler.mbingresskit.common.AgreementsSubsystem
import com.daimler.mbingresskit.common.SoeUserAgreement
import com.daimler.mbingresskit.filestorage.FileWriter
import java.io.File

internal class SoeAgreementsFileStorage(
    context: Context,
    private val fileWriter: FileWriter<ByteArray>
) : BaseAgreementsFileStorage<SoeUserAgreement, ByteArray>(context) {

    override fun writeFile(data: SoeUserAgreement, outFile: File): String? {
        return data.pdfContent?.let {
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
        return deleteAllOfSubsystem(AgreementsSubsystem.SOE)
    }

    override fun getFileExtension(agreement: SoeUserAgreement): AgreementsFileExtension {
        return AgreementsFileExtension.PDF
    }
}