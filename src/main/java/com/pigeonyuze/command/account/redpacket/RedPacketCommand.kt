package com.pigeonyuze.command.account.redpacket

import com.pigeonyuze.MozeBotCore
import com.pigeonyuze.command.account.giftcode.IllegalGiftcodeException
import com.pigeonyuze.util.fromQQ
import com.pigeonyuze.util.quote
import net.mamoe.mirai.console.command.CommandContext
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.util.ConsoleExperimentalApi

object RedPacketCommand : CompositeCommand(
    MozeBotCore,"hb"
){
    @Suppress("UNUSED")
    @OptIn(ConsoleExperimentalApi::class)
    @SubCommand("create")
    suspend fun create(commandContext: CommandContext,
               @Name("红包类型(TEXT, COMMON, LUCK_TEXT, LUCK_COMMON)") type: RedPacket.Type,
               @Name("雨沫币总余额") coin: Int,
               @Name("红包个数") maxUse: Int,
               @Name("红包口令") password: String = "")
    = commandContext.run {
        kotlin.runCatching {
            quote(RedPacket.addRedPacket(type,sender,coin,password,maxUse))
        }.recoverCatching {
            if (it is RedPacketsManger.RedPacketsException || it is IllegalGiftcodeException) {
                quote("无法创建红包，原因: ${it.message}")
                return@run
            }
            quote("在运行时遇到了意想不到的错误\n$it")
        }
    }

    @Suppress("UNUSED")
    @OptIn(ConsoleExperimentalApi::class)
    suspend fun use(commandContext: CommandContext, @Name("红包口令") key: String) = commandContext.run {
        kotlin.runCatching {
            val useRedPacketInfo = RedPacket.useRedPacket(key, fromQQ)
            quote(useRedPacketInfo.userMsg)
        }.recoverCatching {
            if (it is RedPacketsManger.RedPacketsException || it is IllegalGiftcodeException) {
                quote("无法获取红包，原因: ${it.message}")
                return@run
            }
            quote("在运行时遇到了意想不到的错误\n$it")
        }
    }
}