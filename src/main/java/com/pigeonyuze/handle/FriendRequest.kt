package com.pigeonyuze.listener.handle

import com.pigeonyuze.util.UnopenedRunningData
import net.mamoe.mirai.event.events.NewFriendRequestEvent

object FriendRequest: EventHandle<NewFriendRequestEvent> {

    override suspend fun handle(event: NewFriendRequestEvent) {
        val msg = event.message
        val from = event.fromId

        val iterator = UnopenedRunningData.friendCode.iterator()
        while (iterator.hasNext()) {
            val friendCode = iterator.next()
            if (friendCode.obj != from) continue
            if (friendCode.code in msg) {
                event.accept()
                iterator.remove()
            }
        }

        event.reject()
    }
}