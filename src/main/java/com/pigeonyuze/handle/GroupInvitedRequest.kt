package com.pigeonyuze.handle

import com.pigeonyuze.listener.handle.EventHandle
import com.pigeonyuze.util.loggingInfo
import com.pigeonyuze.util.loggingWarn
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent
import com.pigeonyuze.util.BotSetting.authorisedGroupId as authorisedList

object GroupInvitedRequest : EventHandle<BotInvitedJoinGroupRequestEvent> {

    override suspend fun handle(event: BotInvitedJoinGroupRequestEvent) {
        val id = event.groupId
        if (id !in authorisedList) {
            loggingWarn { """
                Unauthorised Invited Request (Automatically ignore) ->
                >fromId: ${event.invitorNick}(${event.invitorId}) by friend? ${event.invitor}
                >to: ${event.groupName}(${event.groupId})
                >this_id: ${event.eventId}
            """.trimIndent() }
            event.ignore()
            return
        }
        event.accept()
        loggingInfo { """
            Authorised Invited Request (Automatically accept) ->
            >fromId: ${event.invitorNick}(${event.invitorId}) by friend? ${event.invitor}
            >to: ${event.groupName}(${event.groupId})
            >this_id: ${event.eventId}
        """.trimIndent()}
    }
}