package com.pigeonyuze.command.account

import com.pigeonyuze.MozeBotCore
import com.pigeonyuze.account.ExperienceUtils
import com.pigeonyuze.account.User
import com.pigeonyuze.account.UserManager
import com.pigeonyuze.util.quote
import net.mamoe.mirai.console.command.CommandContext
import net.mamoe.mirai.console.command.SimpleCommand

object OthersAccountInfoCommand : SimpleCommand(
    MozeBotCore,"get",
    description = "获取他人的账号数据"
){
    @Suppress("UNUSED")
    @Handler
    suspend fun handleId(commandContext: CommandContext, uidOrQQid: Long) = commandContext.run {
        quote(if (UserManager.lastUid >= uidOrQQid) {
            UserManager[uidOrQQid.toInt()].infoMessage()
        }else UserManager.userOf(uidOrQQid)?.infoMessage() ?: "你要寻找该用户不存在哦~")
    }

    private fun User.infoMessage() = """
        $userName 的账号数据如下：
        UID: $uid
        雨沫币: $coin 
        注册时间: $regDate
        等级: Lv.$level[$exp/${ExperienceUtils.getLevelExpData(level)}]
    """.trimIndent()
}