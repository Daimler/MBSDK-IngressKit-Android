package com.daimler.mbingresskit.implementation.filestorage

import com.daimler.mbingresskit.filestorage.FileWriter
import java.io.File

internal class JsonFileWriter : FileWriter<String> {

    override fun readFile(inFile: File): String? {
        return inFile.readText()
    }

    override fun writeToFile(data: String, outFile: File): String? {
        outFile.writeText(data)
        return outFile.absolutePath
    }
}