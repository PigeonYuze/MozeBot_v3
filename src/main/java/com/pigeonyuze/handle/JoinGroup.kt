package com.pigeonyuze.listener.handle

import com.pigeonyuze.listener.handle.DebugInfo.info
import com.pigeonyuze.util.BotSetting
import com.pigeonyuze.util.debugMessage
import net.mamoe.mirai.event.events.BotJoinGroupEvent

object JoinGroup : EventHandle<BotJoinGroupEvent> {
    override suspend fun handle(event: BotJoinGroupEvent) {
        val group = event.group
        if (event.groupId in BotSetting.blackGroupList) {
            group.sendMessage("本群聊已被加入黑名单，不再提供服务")
            group.quit()
            return
        }
        if (event.groupId !in BotSetting.authorisedGroupId) {
            group.sendMessage("未查找到授权记录的群聊，请为此群聊服务授权\n详见官方群聊：542688527")
        }
        debugMessage { "Bot 进入了群聊 ${group.info}" }
        group.sendMessage("( •̀ ω •́ )✧我来力!!!\n不完全的帮助文档: https://www.yuque.com/geziyuze/mozebot")
    }
}