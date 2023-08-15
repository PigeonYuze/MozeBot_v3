package com.pigeonyuze.account

import com.pigeonyuze.account.MemoryUser.Companion.MEMORY_USER_MAPPING_VALUE
import com.pigeonyuze.account.PhysicalUser.Companion.USER_SAVE_FILE
import com.pigeonyuze.account.PhysicalUser.Companion.USER_SAVE_FILE_TMP
import com.pigeonyuze.account.PhysicalUser.Companion.asMemoryUser
import com.pigeonyuze.util.dateFormatter
import com.pigeonyuze.util.launch
import com.pigeonyuze.util.loggingDebug
import com.pigeonyuze.util.loggingVerbose
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.console.command.CommandSender
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import kotlin.concurrent.fixedRateTimer
import kotlin.text.Charsets.UTF_8
import com.pigeonyuze.account.MemoryUser.Companion.data as USER_DATA

object UserManager {
    @JvmStatic
    fun read() {
        val file = USER_SAVE_FILE
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
            ).asMemoryUser()
        }

    }

    operator fun get(uid: Int) = getUidOrNull(uid)?.apply {
        check(this.uid == uid) { "Wrong memory user mapping value!($uid != ${this.uid})" }
    } ?: throw NullPointerException("Can not find user from uid: $uid")

    val lastUid = USER_DATA.lastIndex + MEMORY_USER_MAPPING_VALUE

    private fun getUidOrNull(uid: Int) =
        USER_DATA.getOrNull(if (uid > 10000) uid - MEMORY_USER_MAPPING_VALUE - 1 else uid)

    @JvmStatic
    fun userOf(qqId: Long) =
        if (qqId <= USER_DATA.last().uid) getUidOrNull(qqId.toInt())
        else USER_DATA.firstOrNull { it.qqId == qqId }

    @JvmStatic
    fun CommandSender.regUserOf() = userOf(user?.id ?: 0) ?: MemoryUser(
        USER_DATA.lastOrNull()?.uid?.plus(1) ?: 10000, true,
        0, 0, 0,
        user?.id ?: 0, LocalDateTime.now().format(dateFormatter),
        user?.nick ?: "Console"
    ).apply { reg() }

    @JvmStatic
    fun regUserOf(fromQQ: Long) = userOf(fromQQ) ?: MemoryUser(
        USER_DATA.lastOrNull()?.uid?.plus(1) ?: 10000, true,
        0, 0, 0,
        fromQQ, LocalDateTime.now().format(dateFormatter),
        "不知名用户#${USER_DATA.size}"
    ).apply { reg() }

    enum class WriteOption {
        CLOSE_PLUGIN,
        RUNTIME_TASK
    }

    @JvmStatic
    fun write(status: WriteOption) {
        val job = launch(Dispatchers.IO) {
            var i = 0
            loggingDebug { "Starts to write to temp file..." }
            BufferedWriter(USER_SAVE_FILE_TMP.outputStream().writer(UTF_8)).use {
                for (user in USER_DATA) {
                    i++
                    user.physicalUser!!.write(it, i)
                    loggingVerbose { "Writer -> User#${user.uid}" }
                }
            }
            if (i != USER_DATA.size)
                throw IOException()
            loggingDebug { "Starts to copy temp file to save file..." }
            USER_SAVE_FILE_TMP.copyTo(USER_SAVE_FILE, true)
        }
        if (status == WriteOption.CLOSE_PLUGIN)
            runBlocking {
                job.join()
            }
    }

    fun User.incrementExp(newExp: Long) {
        loggingVerbose { "Adds exp $newExp to $exp -> User#$uid" }
        exp += newExp
    }

    fun User.incrementCoin(newCoin: Int) {
        loggingVerbose { "Adds coins $newCoin to $coin -> User#$uid" }
        coin += newCoin
    }

    init {
        fixedRateTimer(name = "UploadUserData#0", period = 60_0000 * 20L, initialDelay = 60_0000 * 20L) {
            write(WriteOption.RUNTIME_TASK)
        }
    }
}