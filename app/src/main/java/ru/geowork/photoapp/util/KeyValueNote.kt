package ru.geowork.photoapp.util

import java.io.InputStream
import java.io.OutputStream

fun InputStream.readMap(): Map<String, String> = bufferedReader().use { reader ->
    reader.lineSequence()
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .mapNotNull { line ->
            line.split(" - ", limit = 2)
                .takeIf { it.size == 2 }
                ?.let { it[0].trim() to it[1].trim() }
        }
        .toMap()
}

fun OutputStream.writeMap(data: Map<String, String>) = bufferedWriter().use { writer ->
    data.forEach { (key, value) ->
        writer.write("$key - $value")
        writer.newLine()
    }
}
