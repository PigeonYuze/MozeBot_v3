package com.pigeonyuze.listener.handle

import net.mamoe.mirai.event.Event

interface EventHandle<K : Event> {
    suspend fun handle(event: K)
}