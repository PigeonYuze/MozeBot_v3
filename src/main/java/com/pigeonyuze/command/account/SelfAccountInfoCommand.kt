package com.pigeonyuze.command.account

import com.pigeonyuze.MozeBotCore
import com.pigeonyuze.account.User.Companion.showCard
import com.pigeonyuze.account.UserManager
import com.pigeonyuze.util.fromQQ
import com.pigeonyuze.util.quote
import net.mamoe.mirai.console.command.CommandContext
import net.mamoe.mirai.console.command.SimpleCommand

object SelfAccountInfoCommand : SimpleCommand(
    MozeBotCore, "i",
    description = "获取自己的账号数据"
) {
    @Handler
    suspend fun handle(context: CommandContext) = context.run {
        val user = UserManager.regUserOf(fromQQ)
        quote(user.showCard(true))
    }
}