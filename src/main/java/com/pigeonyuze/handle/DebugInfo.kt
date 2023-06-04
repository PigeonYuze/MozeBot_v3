package com.pigeonyuze.listener.handle

import com.pigeonyuze.util.debugMessage
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.event.events.BotLeaveEvent
import net.mamoe.mirai.event.events.BotMuteEvent
import net.mamoe.mirai.event.events.BotUnmuteEvent
import net.mamoe.mirai.utils.MiraiExperimentalApi

object DebugInfo {
    @OptIn(MiraiExperimentalApi::class)
    suspend fun leave(event: BotLeaveEvent) {
        when (event) {
            is BotLeaveEvent.Kick -> debugMessage { "Bot 被 ${event.operator.info} 踢出了群聊 ${event.group.info}" }
            is BotLeaveEvent.Disband -> debugMessage { "Bot 所在的群聊 ${event.group.info} 被解散了" }
            else -> debugMessage { "Bot 离开了群聊 ${event.group.info}" }
        }
    }

    suspend fun mute(event: BotMuteEvent) {
        debugMessage { "Bot[${event.group.botPermission}] 在群聊 ${event.group.info} 被 ${event.operator.info} 禁言了 ${event.durationSeconds}s" }
    }

    suspend fun unmute(event: BotUnmuteEvent) {
        debugMessage { "Bot[${event.group.botPermission}] 在群聊 ${event.group.info} 被 ${event.operator.info} 解除禁言了" }
    }

    val Group.info: String get() = "$name($id)"
    private val Member.info: String get() = "$nick($id)[$permission]"
}