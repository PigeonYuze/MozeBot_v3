package com.pigeonyuze.command.recreation.morse

import com.pigeonyuze.MozeBotCore

import com.pigeonyuze.util.quote
import com.pigeonyuze.util.sendMessage
import com.pigeonyuze.util.uploadAudio
import net.mamoe.mirai.console.command.CommandContext
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource

object MorseCommand : CompositeCommand(
    owner = MozeBotCore,
    primaryName = "morse"
) {
    @Suppress("UNUSED")
    @SubCommand("text")
    suspend fun text(context: CommandContext, text: String) = context.run {
        val textToMorseMsg = MorseConvertor().textToMorseMsg(text)
        quote(textToMorseMsg)
    }

    @Suppress("UNUSED")
    @SubCommand("player")
    suspend fun player(context: CommandContext, text: String) = context.run {
        val audioFile = MozeBotCore.resolveDataFile("audio\\${text.hashCode()}")
        if (!audioFile.exists()) {
            MorseConvertor().textToMorseAudio(text, audioFile.name)
        }
        audioFile.toExternalResource("silk").use {
            val offlineAudio = uploadAudio(it)
            sendMessage(offlineAudio)
        }
    }
}
