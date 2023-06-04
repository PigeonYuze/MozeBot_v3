package com.pigeonyuze.command.phigros.mirai

import com.pigeonyuze.command.phigros.data.PhigrosSongManager.randomSong
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.PlainText
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

object PhigrosRandomCommand {
    suspend fun getRandomSongAndTechnique(subject: Contact): Message {
        val randomSong = randomSong()
        val ret = StringJoiner("\n")
        ret.add("随机曲目：【" + randomSong.masterName + "】")
        ret.add("难度：" + randomSong.level.values.random().toHumanReadableString())
        val ver = randomValue(version)
        ret.add("模式：$ver")
        ret.add("目标：" + randomValue(target))
        if (ver == "课题模式") {
            val randomSong2 = randomSong()
            val randomSong3 = randomSong()
            ret.add("在课题模式下，你还需要打以下歌曲:")
            ret.add(randomSong2.masterName + " " + randomSong2.randomLevelAndOutData() + " 目标：" + randomValue(target))
            ret.add(randomSong3.masterName + " " + randomSong3.randomLevelAndOutData() + " 目标：" + randomValue(target))
        }
        ret.add("需要" + randomValue(play))
        return PlainText(ret.toString()).plus(randomSong.illustrationToImage(subject))
    }

    private fun randomValue(list: List<String>): String {
        return list[Random().nextInt(list.size)]
    }

    private val play: CopyOnWriteArrayList<String> = object : CopyOnWriteArrayList<String>() {
        init {
            add("全程倒打")
            add("单手")
            add("只用左手")
            add("上隐")
            add("只用右手")
            add("全隐")
            add("下半部分隐藏")
            add("盲打")
            add("左半部分隐藏")
            add("全程正打")
            add("正常打法")
            add("女装打")
            add("不穿衣服打")
            add("只用中指打")
            add("只用拇指打")
            add("只用小拇指打")
            add("锁屏打")
        }
    }
    private val version: CopyOnWriteArrayList<String> = object : CopyOnWriteArrayList<String>() {
        init {
            add("课题模式")
            add("普通模式")
        }
    }
    private val target: CopyOnWriteArrayList<String> = object : CopyOnWriteArrayList<String>() {
        init {
            add("φ")
            add("φ")
            add("Full Combo")
            add("V")
            add("蓝V")
            add("All Perfect")
            add("All Good")
            add("All Bad")
            add("All Miss")
            add("S")
            add("S")
            add("A")
            add("B")
            add("C")
            add("False")
        }
    }
}