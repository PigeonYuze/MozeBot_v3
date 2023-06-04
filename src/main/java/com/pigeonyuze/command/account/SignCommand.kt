package com.pigeonyuze.command.account

import com.pigeonyuze.MozeBotCore
import com.pigeonyuze.account.UserManager.incrementCoin
import com.pigeonyuze.account.UserManager.incrementExp
import com.pigeonyuze.account.UserManager.regUserOf
import com.pigeonyuze.util.UnopenedRunningData.signData
import com.pigeonyuze.util.data.LocalDateTime.Companion.toKLocalDateTime
import com.pigeonyuze.util.fromQQ
import com.pigeonyuze.util.quote
import com.pigeonyuze.util.sendMessageChain
import kotlinx.serialization.Serializable
import net.mamoe.mirai.console.command.CommandContext
import net.mamoe.mirai.console.command.SimpleCommand
import java.time.LocalDateTime
import kotlin.random.Random
import com.pigeonyuze.util.data.LocalDateTime as KLocalDateTime

object SignCommand : SimpleCommand(
    MozeBotCore, "sign",
    description = "签到"
) {
    @Suppress("UNUSED")
    @Handler
    suspend fun handleCommand(context: CommandContext) = context.run {
        val user = sender.regUserOf()
        val signedIndex = signData.size + 1
        lateinit var lastedSignData: SignData
        if (signData.firstOrNull { it.signedQQid == fromQQ }?.also { lastedSignData = it } != null) {
            quote("你已经签到过了哦~ \n在 ${lastedSignData.signedTime} 时, 您通过签到获取了 ${lastedSignData.coins} 雨沫币 与 ${lastedSignData.exp} 经验值~")
            return@run
        }
        val coins = when (signedIndex) {
            in 0..3 -> Random.nextInt(15, 20)
            in 3..15 -> Random.nextInt(7, 17)
            else -> Random.nextInt(3, 15)
        }
        val exp = Random.nextLong(100, 500)
        user.incrementCoin(coins)
        user.incrementExp(exp)
        val time = LocalDateTime.now().toKLocalDateTime()
        val now = when (time.hour) {
            in 0..5 -> "凌晨"
            in 5..10 -> "早上"
            in 11..13 -> "中午"
            in 13..19 -> "下午"
            else -> "晚上"
        }
        sendMessageChain {
            +"${now}好! ${user.userName}! \n"
            +"你是今天第 $signedIndex 个签到的\n"
            +"新增雨沫币 $coins 枚 ,经验值 $exp !\n"
            +"签到时间: $time\n"
            +"你现在拥有 ${user.coin} 雨沫币！"
        }
        signData.add(SignData(
            time,fromQQ,coins,exp
        ))
    }

    @Serializable
    data class SignData(
        val signedTime: KLocalDateTime,
        val signedQQid: Long,
        val coins: Int,
        val exp: Long,
    )
}