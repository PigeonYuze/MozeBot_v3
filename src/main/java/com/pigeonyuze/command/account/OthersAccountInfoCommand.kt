package com.pigeonyuze.command.account

import com.pigeonyuze.MozeBotCore
import com.pigeonyuze.account.User.Companion.showCard
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
            UserManager[uidOrQQid.toInt()].showCard(false)
        }else UserManager.userOf(uidOrQQid)?.showCard(false) ?: "你要寻找该用户不存在哦~")
    }

}