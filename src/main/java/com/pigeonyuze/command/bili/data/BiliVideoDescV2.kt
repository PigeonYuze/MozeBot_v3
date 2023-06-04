package com.pigeonyuze.command.bili.data

@kotlinx.serialization.Serializable
data class BiliVideoDescV2(
    val raw_text: String,
    val type: Int,
    val biz_id: Long
)
