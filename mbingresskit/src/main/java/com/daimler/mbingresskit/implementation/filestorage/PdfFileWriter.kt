package com.daimler.mbingresskit.implementation.filestorage

import com.daimler.mbingresskit.filestorage.FileWriter
import java.io.File

internal class PdfFileWriter : FileWriter<ByteArray> {

    override fun writeToFile(data: ByteArray, outFile: File): String? {
        val outStream = outFile.outputStream()
        outStream.write(data)
        outStream.close()
        return outFile.absolutePath
    }

    override fun readFile(inFile: File): ByteArray? {
        val stream = inFile.inputStream()
        val bytes = stream.readBytes()
        stream.close()
        return bytes
    }
}