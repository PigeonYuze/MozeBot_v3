package com.pigeonyuze.command.base

import com.pigeonyuze.MozeBotCore
import com.pigeonyuze.util.quote
import net.mamoe.mirai.console.command.CommandContext
import net.mamoe.mirai.console.command.CompositeCommand
import kotlin.random.Random

object ToolCommand : CompositeCommand(
    MozeBotCore,"tool"
){
    @SubCommand
    @Description("替换原字符串中的部分字符")
    suspend fun replace(commandContext: CommandContext, originalString: String, old: String, new: String = "") = commandContext.run {
        quote(originalString.replace(old,new))
    }

    @SubCommand
    @Description("像 Burn 一样断句")
    suspend fun burn(commandContext: CommandContext, originalString: String) = commandContext.run {
        quote(wrapText(originalString))
    }

    @SubCommand
    @Description("重复一段文本")
    suspend fun copy(commandContext: CommandContext, originalString: String, times: Int) = commandContext.run {
        val sb = StringBuilder()
        repeat(times) {
            sb.append(originalString)
        }
        quote(sb.toString())
    }

    private fun wrapText(input: String): String {
        var currentLine = ""
        val lines = mutableListOf<String>()
        for (b in input) {
            if (!b.isWhitespace() && !b.isInPunctuationSet() && currentLine.length > 4 && Random.nextInt(4) == 0) {
                lines.add(currentLine)
                currentLine = ""
            }
            currentLine += b
        }
        if (currentLine != "") {
            lines.add(currentLine)
        }

        return lines.joinToString(separator = "\n") { it }
    }

    private fun Char.isInPunctuationSet(): Boolean {
        val punctuationSet = setOf(',', '.', ';', ':', '!', '?', '-', '(', ')', '[', ']', '{', '}', '<', '>')
        return punctuationSet.contains(this)
    }
}