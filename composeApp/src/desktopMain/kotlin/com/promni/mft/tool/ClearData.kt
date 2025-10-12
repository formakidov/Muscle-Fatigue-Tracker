package com.promni.mft.tool

import com.promni.mft.data.local.BASE_DB_NAME
import java.io.File

/**
 * A command-line utility to clear the application's database files.
 * This tool specifically targets the database location defined in `DatabaseBuilder.kt`.
 *
 * To run this, open this file in Android Studio / IntelliJ and click the green 'play'
 * button next to the `main` function.
 */
fun main() {
    println("--- MuscleFatigueTracker Data Clearing Tool ---")

    // 1. Get the system's temporary directory, just like the app does.
    val tempDir = File(System.getProperty("java.io.tmpdir"))
    println("Searching for database files in: ${tempDir.absolutePath}")

    // 2. Define the base name of the database file.
    val dbBaseName = "$BASE_DB_NAME.db"

    // 3. Room creates three files. We need to find and delete all of them.
    val dbFile = File(tempDir, dbBaseName)
    val dbShmFile = File(tempDir, "$dbBaseName-shm")
    val dbWalFile = File(tempDir, "$dbBaseName-wal")

    val filesToDelete = listOf(dbFile, dbShmFile, dbWalFile)
    var filesDeletedCount = 0

    // 4. Iterate and delete each file if it exists.
    filesToDelete.forEach { file ->
        if (file.exists()) {
            print("Deleting ${file.name}... ")
            if (file.delete()) {
                println("SUCCESS")
                filesDeletedCount++
            } else {
                println("FAILED")
            }
        }
    }

    if (filesDeletedCount > 0) {
        println("\n✅ Data clearing process completed. $filesDeletedCount file(s) deleted.")
    } else {
        println("\nℹ️ No database files found to clear.")
    }
}
