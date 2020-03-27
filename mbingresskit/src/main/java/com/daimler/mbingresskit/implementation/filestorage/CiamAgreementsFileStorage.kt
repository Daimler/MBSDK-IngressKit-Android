package com.daimler.mbingresskit.implementation.filestorage

import android.content.Context
import com.daimler.mbloggerkit.MBLoggerKit
import com.daimler.mbingresskit.common.AgreementsSubsystem
import com.daimler.mbingresskit.common.CiamUserAgreement
import com.daimler.mbingresskit.filestorage.FileWriter
import java.io.File

internal class CiamAgreementsFileStorage(
    context: Context,
    private val fileWriter: FileWriter<String>
) : BaseAgreementsFileStorage<CiamUserAgreement, String>(context) {

    override fun writeFile(data: CiamUserAgreement, outFile: File): String? {
        return data.htmlContent?.let {
            fileWriter.writeToFile(it, outFile)
        } ?: {
            MBLoggerKit.e("No html content found.")
            null
        }()
    }

    override fun readFile(inFile: File): String? {
        return fileWriter.readFile(inFile)
    }

    override fun deleteFiles(): Boolean {
        return deleteAllOfSubsystem(AgreementsSubsystem.CIAM)
    }

    override fun getFileExtension(agreement: CiamUserAgreement): AgreementsFileExtension {
        return AgreementsFileExtension.HTML
    }
}