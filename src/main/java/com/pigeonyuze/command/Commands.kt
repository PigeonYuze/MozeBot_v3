package com.pigeonyuze.command

import com.pigeonyuze.command.account.OthersAccountInfoCommand
import com.pigeonyuze.command.account.SelfAccountInfoCommand
import com.pigeonyuze.command.account.SignCommand
import com.pigeonyuze.command.account.giftcode.GiftCodeCommand
import com.pigeonyuze.command.account.redpacket.RedPacketCommand
import com.pigeonyuze.command.base.BotCommand
import com.pigeonyuze.command.base.SubmissionCommand
import com.pigeonyuze.command.base.ThrowCoinCommand
import com.pigeonyuze.command.base.ToolCommand
import com.pigeonyuze.command.bili.mirai.BiliCommand
import com.pigeonyuze.command.phigros.PhigrosCommand
import com.pigeonyuze.command.recreation.fb.FbCommand
import com.pigeonyuze.command.recreation.morse.MorseCommand
import com.pigeonyuze.command.recreation.random.BaseRandomCommand
import net.mamoe.mirai.console.command.Command
import net.mamoe.mirai.console.command.CommandManager

object Commands {
    private val commands = listOf<Command>(
        PhigrosCommand,
        BaseRandomCommand,
        ThrowCoinCommand,
        MorseCommand,
        SignCommand,
        SelfAccountInfoCommand,
        OthersAccountInfoCommand,
        GiftCodeCommand,
        RedPacketCommand,
        FbCommand,
        ToolCommand,
        BiliCommand,
        SubmissionCommand,
        BotCommand
    )

    fun load() {
        for (command in commands) {
            CommandManager.registerCommand(command)
        }
    }
}