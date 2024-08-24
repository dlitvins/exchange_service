package com.litvin.exchange

import java.io.File
import java.nio.charset.StandardCharsets

object FileUtils {
    fun getTextFromResource(path: String): String = getResource(path).readText(StandardCharsets.UTF_8)

    private fun getResource(uri: String) = File(ClassLoader.getSystemResource(uri).file)
}
