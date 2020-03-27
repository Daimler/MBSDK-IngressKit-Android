package com.daimler.mbingresskit.implementation.filestorage

import android.content.Context
import com.daimler.mbloggerkit.MBLoggerKit
import com.daimler.mbingresskit.common.AgreementsSubsystem
import com.daimler.mbingresskit.common.UserAgreement
import com.daimler.mbingresskit.filestorage.FileStorage
import java.io.File

internal abstract class BaseAgreementsFileStorage<in T : UserAgreement, out R>(
    private val context: Context
) : FileStorage<T, R?> {

    override fun writeToFile(data: T): String? {
        val dir = cacheDir(data)
        val outFile = File(dir, cachedFileName(data))
        return if (dir.exists() || dir.mkdirs()) {
            try {
                writeFile(data, outFile)
            } catch (e: Exception) {
                MBLoggerKit.e("Failed to writeUserAgreement file to storage.", throwable = e)
                null
            }
        } else {
            MBLoggerKit.e("Cannot create directory ${dir.absolutePath}.")
            null
        }
    }

    override fun writeToFile(data: T, identifier: String): String? {
        val dir = touSubDir(AgreementsSubsystem.UNKNOWN)
        val outFile = File(dir, identifier)
        return if (dir.exists() || dir.mkdirs()) {
            try {
                writeFile(data, outFile)
            } catch (e: Exception) {
                MBLoggerKit.e("Failed to writeUserAgreement file to storage.", throwable = e)
                null
            }
        } else {
            MBLoggerKit.e("Cannot create directory ${dir.absolutePath}.")
            null
        }
    }

    override fun readFromFile(data: T): R? {
        if (data.filePath.isNullOrBlank()) {
            MBLoggerKit.e("No valid file path given.")
            return null
        }
        val file = File(data.filePath)
        return if (file.exists()) {
            try {
                readFile(file)
            } catch (e: Exception) {
                MBLoggerKit.e("Failed to read from file.", throwable = e)
                null
            }
        } else {
            MBLoggerKit.e("File $data does not exist.")
            null
        }
    }

    override fun readFromFile(identifier: String): R? {
        val dir = touSubDir(AgreementsSubsystem.UNKNOWN)
        val inFile = File(dir, identifier)
        return if (inFile.exists()) {
            try {
                readFile(inFile)
            } catch (e: Exception) {
                MBLoggerKit.e("Failed to read from file.", throwable = e)
                null
            }
        } else {
            MBLoggerKit.e("File $inFile does not exist.")
            null
        }
    }

    protected abstract fun writeFile(data: T, outFile: File): String?

    protected abstract fun readFile(inFile: File): R?

    protected abstract fun getFileExtension(agreement: T): AgreementsFileExtension

    private fun touSubDir(subsystem: AgreementsSubsystem): File {
        val cacheDir = File(context.filesDir, TOU_DIR)
        return File(cacheDir, subsystem.name)
    }

    private fun cacheDir(agreement: UserAgreement): File {
        val subDir = touSubDir(agreement.subsystem)
        return File(subDir, "${agreement.locale}/")
    }

    private fun cachedFileName(agreement: T): String =
            "${agreement.displayName ?: agreement.documentId}${getFileExtension(agreement).fileExtension}"

    protected fun deleteAllOfSubsystem(subsystem: AgreementsSubsystem): Boolean {
        val dir = touSubDir(subsystem)
        return dir.deleteRecursively()
    }

    enum class AgreementsFileExtension(val fileExtension: String) {
        HTML(".html"),
        PDF(".pdf")
    }

    private companion object {
        private const val TOU_DIR = "tou/"
    }
}