package com.daimler.mbingresskit.filestorage

/**
 * Generic interface representing a file storage.
 *
 * @param T the type of the input object that should be written to a file
 * @param R the type of the content that should be read of the file
 */
internal interface FileStorage<in T, out R> {

    fun writeToFile(data: T): String?

    fun writeToFile(data: T, identifier: String): String?

    fun readFromFile(data: T): R?

    fun readFromFile(identifier: String): R?

    fun deleteFiles(): Boolean
}