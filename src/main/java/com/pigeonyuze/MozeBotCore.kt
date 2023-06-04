package com.pigeonyuze

import com.pigeonyuze.account.UserManager
import com.pigeonyuze.command.Commands
import com.pigeonyuze.command.bili.mirai.BiliVideoHelper
import com.pigeonyuze.command.phigros.data.PhigrosSongManager
import com.pigeonyuze.command.recreation.fb.FbTextData
import com.pigeonyuze.handle.GroupInvitedRequest
import com.pigeonyuze.listener.handle.*
import com.pigeonyuze.util.quoteTo
import com.pigeonyuze.util.reload
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.Command.Companion.allNames
import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.command.CommandSender.Companion.toCommandSender
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.permission.PermissionId
import net.mamoe.mirai.console.permission.PermissionService
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.EventPriority
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.info

object MozeBotCore : KotlinPlugin(
    JvmPluginDescription(
        id = "com.pigeonyuze.moze-bot",
        name = "MozeBotCore",
        version = "3.0.0",
    ) {
        author("鸽子宇泽")
    }
) {
    val adminPermission by lazy {
        PermissionService.INSTANCE.register(
            PermissionId("com.pigeonyuze.moze-bot","admin"),
            "操作的管理员，内部操作需要该权限",
            MozeBotCore.parentPermission
        )
    }

    @OptIn(ExperimentalCommandDescriptors::class, ConsoleExperimentalApi::class)
    override fun onEnable() {
        logger.info { "Plugin loaded" }
        logger.info { "Started init..." }

        logger.info { "Reload config/data" }
        reload()
        UserManager.read()
        PhigrosSongManager.init()
        FbTextData.reload()

        logger.info { "Register commands" }
        Commands.load()
        val commandsWord by lazy {
            val words = mutableListOf<String>()
            CommandManager.getRegisteredCommands(this@MozeBotCore)
                .onEach { words.addAll(it.allNames) }
            words.map { CommandManager.commandPrefix + it }
        }
        println(commandsWord)

        logger.info { "Started event listener" }
        val eventChannel = GlobalEventChannel.parentScope(this)

        eventChannel.subscribeAlways<MessageEvent>(priority = EventPriority.LOW) {
            message.findIsInstance<LightApp>()?.apply {
                logger.info("Found LightApp check for bili videos")
                BiliVideoHelper.parseQBiliUrl(this)
                    ?.downlandPic()
                    ?.toMiraiMessage(subject)
                    ?.quoteTo(message,subject)
            }
            val content = message.content

            /*
             For MozeBotCore, the commandPrefix of all commands must be '/mz'.
             As a matter of fact, these command may be responded by content start of '/'.
             So cancel some message event, because these messages have wrong command prefix and can be responded by chat-command plugin
            */
            if (commandsWord.any { it.startsWith(content) } && subject is Group) (this as GroupMessageEvent).intercept()

            if (!content.startsWith("/mz")) return@subscribeAlways
            val commandMessage = buildMessageChain {
                for (singleMessage in message) {
                    if (singleMessage is PlainText) {
                        +PlainText(singleMessage.content.replaceFirst("/mz ","/"))
                    } else +singleMessage
                }
            }
            /* Execute command by mirai-console */
            logger.info("Try to running $commandMessage, returned: ${CommandManager.executeCommand(toCommandSender(), commandMessage)}")
        }

        eventChannel.subscribeAlways<MessagePreSendEvent>(priority = EventPriority.HIGH) {
            BotSentMessageCount.handle(this)
        }
        eventChannel.subscribeAlways<BotInvitedJoinGroupRequestEvent> { GroupInvitedRequest.handle(this) }
        eventChannel.subscribeAlways<NewFriendRequestEvent> { FriendRequest.handle(this) }
        eventChannel.subscribeAlways<MemberCardChangeEvent> { MemberCardChange.handle(this) }
        eventChannel.subscribeAlways<BotJoinGroupEvent> { JoinGroup.handle(this) }
        eventChannel.subscribeAlways<BotMuteEvent> { DebugInfo.mute(this) }
        eventChannel.subscribeAlways<BotUnmuteEvent> { DebugInfo.unmute(this) }
        eventChannel.subscribeAlways<BotLeaveEvent> { DebugInfo.leave(this) }
        eventChannel.subscribeAlways<NudgeEvent> {
            if (target !is Bot) return@subscribeAlways
            subject.sendMessage(nudgePlainText.random())
        }
    }

    private val nudgePlainText = listOf(
        "别戳了！！！疼！！！！",
        "你信不信我要生气了！（︶^︶）",
        "扣1送墨泽酱管理员权限(假的)",
        "你信不信....我要叫我主人来了!",
        "是想知道我的功能吗? \n那就去康康https://www.yuque.com/geziyuze/mozebot吧!",
        "定时去世xd",
        "广告位10r一天,详细联系管理员",
        "哼~不理你1秒钟"
    )

    override fun onDisable() {
        UserManager.write()
        logger.info { "Stopped Plugin." }
        super.onDisable()
    }
}