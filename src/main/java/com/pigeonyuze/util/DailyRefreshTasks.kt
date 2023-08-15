package com.pigeonyuze.util

import com.pigeonyuze.util.DailyRefreshTasks.Refresh
import kotlinx.coroutines.CoroutineStart
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Date
import kotlin.concurrent.fixedRateTimer
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KParameter
import kotlin.reflect.KParameter.Kind
import kotlin.reflect.KProperty1
import kotlin.reflect.KVisibility
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.callSuspendBy
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

/**
 * ### 自动更新数据
 *
 * 使用 [Refresh] 标注自动更新的目标[KProperty1]，此目标**必须**为可变参数(`var`)
 *
 * 之后的每一次数据都将于**每日的0时准点**，修改为**初始化时的初始数据**
 *
 * 当目标未初始化时，始终设置为`null`
 *
 * **你需要通过 [initRefresh] 加载后，功能才会生效**
 *
 * 示例:
 * ```kotlin
 * object Example : DailyDailyRefreshTasks {
 *      init {
 *          initRefresh()
 *      }
 *
 *      @Refresh
 *      var data: Int by lazy { 0 }
 *  }
 * ```
 * - 支持`suspend`的`<getter>`
 *
 * */
interface DailyRefreshTasks {

    @Target(AnnotationTarget.PROPERTY)
    annotation class Refresh

    /**
     * 将标注有 [Refresh] 的 [KProperty1] 存入待修改列表
     *
     * 同时存储当前的值(通过`<getter>`函数)
     * */
    @Suppress("UNCHECKED_CAST")
    fun DailyRefreshTasks.initRefresh() {
        fun getPropertiesWithGetter() =
            this@initRefresh::class.memberProperties
                .asSequence()
                .filter { it.findAnnotation<Refresh>() != null }
                .onEach {
                    require(it is KMutableProperty1<*, *>)
                      { "Automatically refresh property must be mutable" }
                    require(it.getter.parameters.size <= 1)
                      { "The parameters of <property-getter> must at most one" }
                    require(it.visibility != KVisibility.PRIVATE)
                      { "<property-getter> function must be accessible from initRefresh, that is, effectively public" }
                }
                .associateBy { it.getter }

        /* Coroutine scheduling always takes time */
        /* If coroutineStart is not UNDISPATCHED, then may be inaccuracies in information */
        launch(start = CoroutineStart.UNDISPATCHED) {
            for ((getter, properties: KProperty1<out DailyRefreshTasks, Any?>) in getPropertiesWithGetter()) {
                properties as KMutableProperty1<out DailyRefreshTasks, Any?>
                val initValue =
                    if (getter.isSuspend) getter.callSuspend(this@initRefresh)
                    else getter.call(this@initRefresh)

                loggingDebug { "InitValue of Property(with getter $getter): $initValue" }
                functionRefreshObject.add(
                    RefreshProperty(this@initRefresh,properties,initValue)
                )
            }
        }

        loggingInfo { "After init values. \n'refresh_objects': {\n${functionRefreshObject.joinToString(",\n") { (key,property, value) ->
            "\t{\n\t\t'task.class': '${key.javaClass}',\n\t\t'mutable_property1_element': '$property',\n\t\t'value': $value\n\t}"
        }}\n}" }
    }


    private data class RefreshProperty<K : DailyRefreshTasks, V : Any?>(
        val instance: K,
        val property: KMutableProperty1<out K, V>,
        val initValue: V,
    )

    private companion object {
        val functionRefreshObject: MutableList<RefreshProperty<out DailyRefreshTasks, Any?>> = mutableListOf()

        const val HOURS_OF_DAY = 24
        const val MINUTES_OF_HOUR = 60
        const val SECOND_OF_MINUTE = 60
        const val MILLIS_OF_SECOND = 1000L
        const val A_DAY_MILLIS = HOURS_OF_DAY * MINUTES_OF_HOUR * SECOND_OF_MINUTE * MILLIS_OF_SECOND

        init {
            fixedRateTimer(daemon = true, initialDelay = getMillisUntilMidnight(), period = A_DAY_MILLIS) {
                modifyDataAtMidnight()
            }
        }

        private fun getMillisUntilMidnight(): Long {
            val now = LocalDateTime.now()
            val midnight = now.plusDays(1).with(LocalTime.MIDNIGHT).minusSeconds(1)
            loggingVerbose {
                "'local_date_time': {\n\t'now': $now,\n\t'target': $midnight\n}"
            }
            val duration = Duration.between(now, midnight)
            loggingVerbose { "'duration': $duration" }
            val result = duration.toMillis()
            loggingInfo {
                "Got initial delay, $result. " +
                        "(as java.utils.Date: ${Date(System.currentTimeMillis() + result)})" }
            return result
        }

        private fun modifyDataAtMidnight() {
            fun getSetterWithArgs() = functionRefreshObject
                .asSequence()
                .onEach {
                    require(it.property.parameters.size <= 2)
                        { "The parameters of <property-getter> must at most two" }
                }
                .associate { refreshProperty ->
                    loggingVerbose{
                        "Before setting {field ${refreshProperty.property}}: " +
                                "${refreshProperty.property.getter.call(refreshProperty.instance)}"
                    }
                    val setter = refreshProperty.property.setter
                    val args = mutableMapOf<KParameter, Any?>()
                    setter.parameters.forEach {
                        check(it.kind != Kind.EXTENSION_RECEIVER) {
                            "The parameters of <property-getter> must not be a extension function"
                        }
                        when (it.kind) {
                            Kind.INSTANCE -> args[it] = refreshProperty.instance
                            else -> args[it] = refreshProperty.initValue
                        }
                    }
                    setter to args
                }

            /* Ensure the punctuality of tasks, Avoid unnecessary time spent due to coroutine scheduling */
            launch(start = CoroutineStart.UNDISPATCHED) {
                loggingInfo { "launch starting: set values.(with time: ${System.currentTimeMillis()})" }
                for ((setter, args) in getSetterWithArgs()) {
                    if (setter.isSuspend) setter.callSuspendBy(args)
                    else setter.callBy(args)
                }
                loggingInfo { "Done." }
            }
        }
    }
}
