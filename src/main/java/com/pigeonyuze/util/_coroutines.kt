package com.pigeonyuze.util

import com.pigeonyuze.MozeBotCore
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

internal var debugging = false

/**
 * Mapping to [MozeBotCore.launch]
 * */
@OptIn(DelicateCoroutinesApi::class)
internal fun launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job {
    return if (!debugging) MozeBotCore.launch(context, start, block)
    else GlobalScope.launch(context, start, block)
}