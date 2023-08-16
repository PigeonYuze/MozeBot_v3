package com.pigeonyuze.account

import java.time.LocalTime

/**
 * ### 用户的实现
 * */
interface User {
    val uid: Int
    val qqId: Long
    val regDate: String
    var coin: Int
    var exp: Long
    var level: Int
    var userName: String
    var canFreeSetName: Boolean

    companion object {
        private const val level1Data: Long = 1000

        private const val level2Data: Long = 3000

        private const val level3Data: Long = 5000

        private const val level4Data: Long = 8000

        private const val level5Data: Long = 12000

        private const val level6Data: Long = 50000

        private const val level7Data: Long = 100000

        fun Long.ofLevel() =
            when (this) {
                in 0 until level1Data -> 0
                in level1Data until level2Data -> 1
                in level2Data until level3Data -> 2
                in level3Data until level4Data -> 3
                in level4Data until level5Data -> 4
                in level5Data until level6Data -> 5
                in level6Data until level7Data -> 6
                else -> if (this < 0) throw IllegalArgumentException() else 7
            }

        private fun ofExp(level: Int): Long? =
            when (level) {
                0 -> 0
                1 -> level1Data
                2 -> level2Data
                3 -> level3Data
                4 -> level4Data
                5 -> level5Data
                6 -> level6Data
                7 -> level7Data
                8 -> null
                else -> throw IllegalArgumentException()
            }

        fun User.showCard(isBySelf: Boolean): String {
            val isMaxLevel = level == 7
            val greetingText = when (LocalTime.now().hour) {
                in 0..4 -> "晚~~凌晨好.."
                in 5..11 -> "早啊~"
                in 12..14 -> "中午好！"
                in 15..19 -> "下午了呢？"
                in 20..23 -> "晚好啊~"
                else -> throw IllegalArgumentException()
            }
            return """
                $greetingText
                ${if (isBySelf) "$userName ！" else "$userName "}
                UID: $uid
                雨沫币: $coin 
                注册时间: $regDate
                等级: Lv.$level${
                if (isMaxLevel) " (${if (isBySelf) "您" else "TA"}已经是最高等级了)" else "$exp/${
                    ofExp(
                        level + 1
                    )
                }"
            }]
            """.trimIndent()
        }
    }
}
