@file:JvmName("LoggerManager")
package com.pigeonyuze.util

import com.pigeonyuze.MozeBotCore.logger
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.utils.verbose

fun loggingInfo(from: String,message: String) {
    logger.info("$from: $message")
}

fun loggingInfo(message: () -> String) {
    loggingInfo("runtime",message.invoke())
}

inline fun loggingDebug(message: () -> String) {
    logger.debug(message.invoke())
}

inline fun loggingVerbose(message: () -> String) {
    logger.verbose(message)
}

fun loggingWarn(from: String,message: String,callToGroup: Boolean = true) {
    if (!callToGroup) {
        logger.warning("$from: $message")
        return
    }
    launch {
        debugGroup.sendMessage(
            """
            ?> Warning Logging Message [$from]
            message = $message
            """.trimIndent()
        )
    }
}

fun loggingWarn(message: () -> String) {
    logger.warning(message.invoke())
    launch {
        debugGroup.sendMessage(
            """
            ?> Warning Logging Message
            ${message.invoke()}
            """.trimIndent()
        )
    }
}

fun loggingError(e: Throwable) {
    logger.error(e)
    launch {
        debugGroup.sendMessage(
            """
            !> Error Logging Message
            ${e.stackTrace}
            """.trimIndent()
        )
    }
}

suspend fun debugMessage(message: () -> String) {
    debugGroup.sendMessage(
        """
            ?> Debug message
            ${message.invoke()}
            """.trimIndent()
    )
}

suspend fun debugMessage(message: String) {
    debugGroup.sendMessage(
        """
            ?> Debug message
            $message
            """.trimIndent()
    )
}

fun debugMessage(message: Message) {
    launch {
        debugGroup.sendMessage(message)
    }
}


private val debugGroup by lazy {
    lateinit var group: Group
    Bot.instances.forEach {
        group = it.getGroup(BotSetting.masterDebugGroupId) ?: return@forEach
    }
    return@lazy group
}

