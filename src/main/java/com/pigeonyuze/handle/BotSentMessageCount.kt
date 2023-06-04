package com.pigeonyuze.listener.handle

import com.pigeonyuze.util.BotSendMessageConfig
import com.pigeonyuze.util.launch
import com.pigeonyuze.util.loggingWarn
import kotlinx.coroutines.delay
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.MessagePreSendEvent
import java.util.concurrent.ConcurrentHashMap
import com.pigeonyuze.util.BotSendMessageConfig.defMaxSentCount as defMaxValue

object BotSentMessageCount : EventHandle<MessagePreSendEvent> {

    /* Long - target ID, Int - value */
    private val sendToObject = ConcurrentHashMap<Long, Int>()

    /* Long - target ID, Int - maxValue */
    private val cacheMaxValue = ConcurrentHashMap<Long, Int>()
    private val cacheMaxValueToCalledCount = ConcurrentHashMap<Long, Int>()


    init {
        launch {
            while (true) {
                delay(60_000)
                clearCache()
            }
        }
    }


    private fun clearCache() {
        sendToObject.clear()
        cacheMaxValue.clear()
        cacheMaxValueToCalledCount.clear()
    }

    override suspend fun handle(event: MessagePreSendEvent) {
        if (!BotSendMessageConfig.openMessageShielding) return
        if ((BotSendMessageConfig.isOnlySendToGroup && event.target !is Group) || (event.target !is Friend && event.target !is Group)) {
            event.cancel()
            return
        }
        val target = event.target.id
        val value = sendToObject.compute(target) { _, oldValue -> (oldValue ?: 0) + 1 } ?: 0
        val targetMaxValue = cacheMaxValue.computeIfAbsent(target) { getMaxValue(target) }
        if (value >= targetMaxValue) {
            loggingWarn { "[Sending message too fast] by target : $target (${value}/${targetMaxValue}). Auto canceled !" }
            event.cancel()
            return
        }

        /* The cache might start again */
        launch {
            cacheMaxValueToCalledCount.compute(target) { _, oldValue -> (oldValue ?: 0) + 1 }
        }
    }

    private fun getMaxValue(target: Long): Int {
        return defMaxValue[target] ?: 70
    }

}
