package com.pigeonyuze.command.recreation.fb

import com.pigeonyuze.MozeBotCore
import com.pigeonyuze.util.*
import net.mamoe.mirai.console.command.CommandContext
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.message.data.buildForwardMessage
import net.mamoe.mirai.message.data.sendTo
import net.mamoe.mirai.message.data.toPlainText
import kotlin.random.Random

object FbCommand: SimpleCommand(
    MozeBotCore,"fb"
) {
    @Handler
    suspend fun handle(commandContext: CommandContext, who: String) = commandContext.run{
        val index = Random.nextInt(FbTextData.texts.size)
        val randomObject = FbTextData.texts[index]

        val random = "[MozeBot] 发病语录(#$index by ${randomObject.from})\n${randomObject.fb(who)}"
        if (sender.isByConsole) {
            sendMessage(random.toPlainText())
            return@run
        }
        if (random.length > 300) {
            buildForwardMessage(subject) {
                from!! says random
            }.sendTo(subject)
        }else quote(random)
    }
}