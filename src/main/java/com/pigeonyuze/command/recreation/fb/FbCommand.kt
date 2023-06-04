package com.pigeonyuze.command.recreation.fb

import com.pigeonyuze.MozeBotCore
import com.pigeonyuze.util.from
import com.pigeonyuze.util.quote
import com.pigeonyuze.util.subject
import net.mamoe.mirai.console.command.CommandContext
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.message.data.buildForwardMessage
import net.mamoe.mirai.message.data.sendTo
import kotlin.random.Random

object FbCommand: SimpleCommand(
    MozeBotCore,"fb"
) {
    @Handler
    suspend fun handle(commandContext: CommandContext, who: String) = commandContext.run{
        val index = Random.nextInt(FbTextData.texts.size)

        val random = "[MozeBot] 发病语录(#$index)\n" + FbTextData.texts[index].fb(who)
        if (random.length > 300) {
            buildForwardMessage(subject) {
                from!! says random
            }.sendTo(subject)
        }else quote(random)
    }
}