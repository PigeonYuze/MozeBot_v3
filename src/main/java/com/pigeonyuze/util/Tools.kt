package com.pigeonyuze.util

import com.pigeonyuze.MozeBotCore
import java.io.File
import java.net.URL
import java.time.format.DateTimeFormatter

val hmsFormatter: DateTimeFormatter by lazy { DateTimeFormatter.ofPattern("HH:mm:ss") }
val dateFormatter: DateTimeFormatter by lazy { DateTimeFormatter.ofPattern("yyyy-MM-ss HH:mm:ss") }

internal fun readResourcesFile(name: String): String {
    return MozeBotCore::class.java.getResource(name)?.readText() ?: throw IllegalAccessError("文件不存在")
}
internal fun resourcesFileOf(name: String): File {
    fun URL.toFile() = File(this.toURI())
    return MozeBotCore::class.java.getResource(name)?.toFile() ?: throw IllegalAccessError("文件不存在")
}

internal fun resourcesAsMiraiData(name: String) {
    val resolve by lazy { MozeBotCore.dataFolderPath.resolve("resources") }
    val resourcesFile = resourcesFileOf(name)
    val miraiDataFile = resolve.resolve(name).toFile()
    if (miraiDataFile.exists()) {
        return
    }
    resourcesFile.copyTo(miraiDataFile)
}

infix fun <A : Any, B : Any> A.mapTo(that: B): Map<A, B> = mapOf(Pair(this,that))
