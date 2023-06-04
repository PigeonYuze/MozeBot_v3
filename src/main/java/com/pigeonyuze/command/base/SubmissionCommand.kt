package com.pigeonyuze.command.base

import com.pigeonyuze.MozeBotCore
import com.pigeonyuze.account.MemoryUser
import com.pigeonyuze.account.UserManager
import com.pigeonyuze.command.recreation.fb.FbTextData
import com.pigeonyuze.util.dateFormatter
import com.pigeonyuze.util.fromQQ
import com.pigeonyuze.util.quote
import net.mamoe.mirai.console.command.CommandContext
import net.mamoe.mirai.console.command.CompositeCommand
import java.time.LocalDateTime

object SubmissionCommand : CompositeCommand(
    MozeBotCore,"submit"
) {
    @SubCommand("fb")
    @Description("投稿发病文本，用%s代替发病对象")
    suspend fun forFbText(commandContext: CommandContext,text: String) = commandContext.run {
        val newFbText = FbTextData.FbText(
            text,
            UserManager.regUserOf(fromQQ).nameWithId(),
            LocalDateTime.now().format(dateFormatter)
        )
        FbTextData.texts.add(newFbText)
        quote("已成功投稿并被收入为fb语录中(index=${FbTextData.texts.lastIndex})")
    }

    private fun MemoryUser.nameWithId() = "$userName($uid)"

}