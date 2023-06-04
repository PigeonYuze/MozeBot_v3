package com.pigeonyuze.command.bili.data

import kotlinx.serialization.Serializable

@Serializable
data class VideoStat(
    val aid: Long,
    val view: Long,
    val danmaku: Int,
    val reply: Int,
    val favorite: Int,
    val coin: Int,
    val share: Int,
    val now_rank: Int,
    val his_rank: Int,
    val like: Int,
    val dislike: Int,
    val evaluation: String,
    val argue_msg: String,
) {
    fun toHamanReadableString(): String {
        val builder = StringBuilder()
        if (argue_msg.isNotEmpty()) {
            builder.append("『${argue_msg}』")
            builder.append('\n')
        }
        builder.append("点赞量:$like ")
        builder.append("硬币量:$coin ")
        builder.append("转发量:$reply ")
        builder.append("收藏量:$favorite ")
        builder.append("弹幕量:$danmaku")
        if (his_rank.or(now_rank) != 0) {
            return builder.toString()
        }
        if (his_rank != 0) {
            builder.append('\n')
            builder.append("历史全站最高排名 #$his_rank")
            if (now_rank != 0) {
                builder.append("(当前全站排名 #$now_rank)")
            }
            return builder.toString()
        }
        builder.append("当前全站排名 #$now_rank")
        return builder.toString()
    }
}
