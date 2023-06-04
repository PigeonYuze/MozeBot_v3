package com.pigeonyuze.util.data

import java.time.LocalDateTime as JLocalDateTime

/**
 * Mapping to [JLocalDateTime]
 * */
@kotlinx.serialization.Serializable
data class LocalDateTime(
   val year: Int,
   val month: Int,
   val dayOfMonth: Int,
   val hour: Int,
   val minute: Int,
   val second: Int
) {
    fun toJLocalDateTime(): JLocalDateTime = JLocalDateTime.of(
        year, month, dayOfMonth, hour, minute, second
    )


    override fun toString(): String {
        return "$year-$month-$dayOfMonth $hour:$minute:$second"
    }
    companion object {
        fun JLocalDateTime.toKLocalDateTime() = LocalDateTime(
            year,month.value,dayOfMonth,hour,minute,second
        )
    }
}
