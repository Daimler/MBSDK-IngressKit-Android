package com.daimler.mbingresskit.implementation.filestorage

import com.daimler.mbingresskit.filestorage.FileWriter
import java.io.File

internal class HtmlFileWriter : FileWriter<String> {

    override fun writeToFile(data: String, outFile: File): String? {
        val outStream = outFile.outputStream()
        outStream.write(data.toByteArray())
        outStream.close()
        return outFile.absolutePath
    }

    override fun readFile(inFile: File): String? {
        return inFile.readText()
    }
}