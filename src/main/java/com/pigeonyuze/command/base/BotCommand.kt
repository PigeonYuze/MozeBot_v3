package com.pigeonyuze.command.base

import com.pigeonyuze.MozeBotCore
import com.pigeonyuze.account.MemoryUser
import com.pigeonyuze.util.*
import net.mamoe.mirai.console.command.CommandContext
import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.permission.PermissionService.Companion.hasPermission
import net.mamoe.mirai.console.plugin.*
import net.mamoe.mirai.console.util.SemVersion
import net.mamoe.mirai.contact.PermissionDeniedException
import net.mamoe.mirai.contact.isAdministrator
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain

object BotCommand : CompositeCommand(
    MozeBotCore, "bot",
    description = "关于`Bot`的相关内容及部分非开发功能"
) {

    @SubCommand
    @Description("获取所有指令的描述")
    suspend fun help(commandContext: CommandContext) = commandContext.run {
        quote(CommandManager.allRegisteredCommands.joinToString("\n") {
            it.usage
        })
    }

    @SubCommand("plugins")
    @Description("查询插件列表")
    suspend fun queryPluginsInfo(commandContext: CommandContext) = commandContext.run {
        val modules =
            PluginManager.plugins
                .filter { it.version != SemVersion.invoke("0.1.0") }
        sendForwardMessage {
            modules.forEach { plugin ->
                from!! says """
                    ${plugin.name} [${plugin.id}]
                    Version: ${plugin.version}
                    ${plugin.info}
                    Author(s): ${plugin.author}
                """.trimIndent()
            }
        }
    }

    @SubCommand("groups")
    @Description("[查询所有群聊]")
    suspend fun queryGroupsInfo(commandContext: CommandContext) = commandContext.run {
        sender.hasPermission(MozeBotCore.adminPermission) || throw PermissionDeniedException()

        val groups = bot?.groups ?: return@run
        sendForwardMessage {
            groups.forEachIndexed { index, group ->
                from!! says """
                    #$index【${group.name}】 (id: ${group.id})
                    Owner: ${group.owner}
                    Administrator(s): ${group.members.filter { it.isAdministrator() }.joinToString()}
                    Permission of running bot: ${group.botPermission}
                    (MuteRemaining?: ${group.botMuteRemaining})
                    AvatarUrl: ${group.avatarUrl}
                """.trimIndent()
            }
        }
    }

    @SubCommand("friends")
    @Description("[查询所有好友]")
    suspend fun queryFriendsInfo(commandContext: CommandContext) = commandContext.run {
        sender.hasPermission(MozeBotCore.adminPermission) || throw PermissionDeniedException()

        val friends = bot?.friends ?: return@run
        sendForwardMessage {
            friends.forEachIndexed { index, friend ->
                from!! says """
                    #$index【${friend.nick}】 (id: ${friend.id})
                    In group: ${friend.friendGroup.name}(${friend.friendGroup.id})
                    AvatarUrl: ${friend.avatarUrl}
                """.trimIndent()
            }
        }
    }

    @SubCommand("mass")
    @Description("[群发信息]")
    suspend fun massMessageToAllGroups(commandContext: CommandContext,message0: MessageChain) = commandContext.run {
        sender.hasPermission(MozeBotCore.adminPermission) || throw PermissionDeniedException()
        val message = buildMessageChain {
            for (element in message0) {
                if (element is PlainText && element.content.startsWith("/mass")) {
                    +PlainText(element.content.removePrefix("/mass "))
                }else + element
            }
        }

        val groups = bot?.groups ?: return@run
        groups.forEach {
            it.sendMessage(message)
        }
    }

    @SubCommand("perm group")
    suspend fun permGroup(commandContext: CommandContext,obj: Long) = commandContext.run {
        sender.hasPermission(MozeBotCore.adminPermission) || throw PermissionDeniedException()

        BotSetting.authorisedGroupId.add(obj)
        quote("Done.\n${BotSetting.authorisedGroupId}")
    }

    @SubCommand("users data")
    suspend fun userData(commandContext: CommandContext) = commandContext.run {
        sender.hasPermission(MozeBotCore.adminPermission) || throw PermissionDeniedException()

        sendMessage(PlainText(MemoryUser.data.joinToString("\n") {
            "${it.uid},${it.userName},${it.coin},${it.regDate},${it.exp},${it.qqId},${it.level},${it.canFreeSetName}"
        }))
    }
}