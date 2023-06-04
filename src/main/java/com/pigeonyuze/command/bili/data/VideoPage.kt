package com.pigeonyuze.command.bili.data

import kotlinx.serialization.Serializable

@Serializable
data class VideoPage(
    val cid: Int,
    val page: Int,
    val from: String,
    val part: String,
    val duration: Int,
    val vid: String,
    val weblink: String
)
