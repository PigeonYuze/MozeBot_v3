package com.pigeonyuze.command.phigros.data

import me.him188.kotlin.jvm.blocking.bridge.JvmBlockingBridge
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import org.apache.commons.text.similarity.JaroWinklerDistance
import com.pigeonyuze.command.phigros.data.PhigrosSongLevel.Level
import java.io.File
import java.util.*

@kotlinx.serialization.Serializable
data class PhigrosSong(
    val chapter: String,
    val bpm: Double,
    val songAuthor: String,
    val illustration: String,
    val masterName: String,
    val names: List<String>,
    val unlock: String,
    val from: String,
    val level: Map<Level, PhigrosSongLevel>,
    val songLength: String,
    val illustrationPath: String,
) {
    val atLevel by lazy { level[Level.AT]!! }
    val inLevel by lazy { level[Level.IN]!! }
    val hdLevel by lazy { level[Level.HD]!! }
    val ezLevel by lazy { level[Level.EZ]!! }

    fun isAtLevelExists() = level.contains(Level.AT)

    private fun levelToString(): String {
        val stringBuilder = StringBuilder()

        if (isAtLevelExists()) stringBuilder.append("AT 难度\n${level[Level.AT]!!.toHumanReadableString()}")
        stringBuilder.append("IN 难度\n")
        stringBuilder.append(level[Level.IN]?.toHumanReadableString())
        stringBuilder.append("\nHD 难度\n")
        stringBuilder.append(level[Level.HD]?.toHumanReadableString())
        stringBuilder.append("\nEZ 难度\n")
        stringBuilder.append(level[Level.EZ]?.toHumanReadableString())
        return stringBuilder.toString()
    }

    fun matchingEqual(name: String): Boolean {
        val checkName = name.trim().lowercase(Locale.getDefault())
        val jaroWinklerDistance = JaroWinklerDistance()
        var distance = jaroWinklerDistance.apply(
            checkName, masterName
        )
        if (distance < 0.30) {
            return true
        }

        for (uncheckedName in names) {
            if (jaroWinklerDistance.apply(checkName, uncheckedName).also { distance = it } < 0.210) {
                return true
            }
        }
        return false
    }

    fun humanReadable() =
        """$masterName [$from]
BPM: $bpm
曲师: $songAuthor
章节: $chapter
曲绘画师: $illustration
歌曲时长: $songLength
解锁方式: $unlock
${levelToString()}"""

    @JvmBlockingBridge
    suspend fun toMiraiMessage(contact: Contact) = buildMessageChain {
        +humanReadable()
        +illustrationToImage(contact)
    }

    fun randomLevelAndOutData(): String {
        val random = Random()
        val randomLevelNumber = random.nextInt(12)
        if (randomLevelNumber < 2) {
            return "【EZ难度】 ${ezLevel.toHumanReadableString()}"
        }
        return if (randomLevelNumber < 5) {
            "【HD难度】 ${hdLevel.toHumanReadableString()}"
        } else {
            if (isAtLevelExists()) {
                if (randomLevelNumber < 9) return "【AT难度】 ${atLevel.toHumanReadableString()}"
            }
            return "【IN难度】 ${inLevel.toHumanReadableString()}"
        }
    }


    suspend fun illustrationToImage(contact: Contact): Image {
        lateinit var image: Image
        File(illustrationPath).toExternalResource().use {
            image = contact.uploadImage(it)
        }
        return image
    }

    companion object {
        @JvmField
        val nullObject: PhigrosSong = PhigrosSong(
            "null",
            -1.0,
            "null",
            "null",
            "null",
            listOf("null"),
            "null",
            "null",
            mapOf(),
            "null",
            "null",
        )
    }
}
