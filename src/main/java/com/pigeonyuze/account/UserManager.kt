package com.pigeonyuze.account

import com.pigeonyuze.account.MemoryUser.Companion.MEMORY_USER_MAPPING_VALUE
import com.pigeonyuze.account.PhysicalUser.Companion.asMemoryUser
import com.pigeonyuze.util.dateFormatter
import net.mamoe.mirai.console.command.CommandSender
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import kotlin.apply
import com.pigeonyuze.account.MemoryUser.Companion.data as USER_DATA

object UserManager {
    @JvmStatic
    fun read() {
        val file = PhysicalUser.USER_SAVE_FILE
        file.createNewFile()
        val br = BufferedReader(
            InputStreamReader(
                file.inputStream(),
                StandardCharsets.UTF_8
            )
        )
        var row = ""
        var i = 0
        while (br.readLine()?.also { row = it } != null) {
            i++
            PhysicalUser(
                i + MEMORY_USER_MAPPING_VALUE,
                i,
                row
            ).asMemoryUser().mappingToData()
        }

    }

    operator fun get(uid: Int) = USER_DATA[if (uid > 10000) uid - MEMORY_USER_MAPPING_VALUE else uid].apply {
        check(this.uid == uid) { "Wrong memory user mapping value!" }
    }
    val lastUid = USER_DATA.lastIndex + MEMORY_USER_MAPPING_VALUE

    @JvmStatic
    fun userOf(qqId: Long)
        = USER_DATA.firstOrNull { it.qqId == qqId }

    @JvmStatic
    fun CommandSender.regUserOf() = userOf(user?.id ?: 0) ?: MemoryUser(
        USER_DATA.size + MEMORY_USER_MAPPING_VALUE, true,
        0, 0, 0,
        user?.id ?: 0, LocalDateTime.now().format(dateFormatter),
        user?.nick ?: "Console"
    ).apply { reg() }

    @JvmStatic
    fun regUserOf(fromQQ: Long) = userOf(fromQQ) ?: MemoryUser(
        USER_DATA.size + MEMORY_USER_MAPPING_VALUE, true,
        0, 0, 0,
        fromQQ, LocalDateTime.now().format(dateFormatter),
        "不知名用户#${USER_DATA.size}"
    ).apply { reg() }

    @JvmStatic
    fun write() {
        for (user in USER_DATA) {
            user.sync()
        }
    }

    fun User.incrementExp(newExp: Long) {
        exp += newExp
    }

    fun User.incrementCoin(newCoin: Int) {
        coin += newCoin
    }

    fun User.incrementLevel(newLevel: Int) {
        level += newLevel
    }


}