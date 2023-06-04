package com.pigeonyuze.command.account

import com.pigeonyuze.MozeBotCore
import com.pigeonyuze.account.ExperienceUtils
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
        user.run {
            if (level != ExperienceUtils.getLevel(exp)) {
                level = ExperienceUtils.getLevel(exp)
            }
            quote(
                """
                $userName ！
                您的账号数据如下：
                UID: $uid
                雨沫币: $coin 
                注册时间: $regDate
                等级: Lv.$level[$exp/${ExperienceUtils.getLevelExpData(level)}]
            """.trimIndent()
            )
        }
    }
}