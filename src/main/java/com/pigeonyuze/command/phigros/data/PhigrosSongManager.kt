package com.pigeonyuze.command.phigros.data

import com.pigeonyuze.util.readResourcesFile
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.*

object PhigrosSongManager {
    @JvmField
    val PHIGROS_SONG_LIST: MutableList<PhigrosSong> = ArrayList()
    val CHAPTER_SONG_MAP: MutableMap<String, MutableList<PhigrosSong>> = HashMap()

    fun init() {
        val text: String = readResourcesFile("/pgr/phigros.json")
        Json.decodeFromString<List<PhigrosSong>>(text).forEach {
            PHIGROS_SONG_LIST.add(it)
            val value = CHAPTER_SONG_MAP.getOrDefault(it.chapter, mutableListOf())
            value.add(it)
        }
    }

    @JvmStatic
    fun randomSong(): PhigrosSong {
        return PHIGROS_SONG_LIST[Random().nextInt(PHIGROS_SONG_LIST.size)]
    }

    @JvmStatic
    fun findSongOrNullObject(obj: String?): PhigrosSong {
        if (PHIGROS_SONG_LIST.isEmpty()) return PhigrosSong.nullObject
        for (phigrosSong in PHIGROS_SONG_LIST) {
            if (phigrosSong.matchingEqual(obj ?: "null")) {
                return phigrosSong
            }
        }
        return PhigrosSong.nullObject
    }

}