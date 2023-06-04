package com.pigeonyuze.command.bili.mirai

import com.pigeonyuze.MozeBotCore
import com.pigeonyuze.util.quote
import net.mamoe.mirai.console.command.CommandContext
import net.mamoe.mirai.console.command.CompositeCommand

object BiliCommand : CompositeCommand(
    MozeBotCore,"bili"
){
    @SubCommand("bv")
    suspend fun queryByBvid(commandContext: CommandContext, bvid: String) = commandContext.run {
        kotlin.runCatching {
            BiliVideoHelper.requestVideo(bvid)
        }.recoverCatching {
            quote("无法获取视频详情，原因: \n${it.message}")
            throw it
        }
    }
}