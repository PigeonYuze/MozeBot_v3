package com.pigeonyuze.command.bili.data

@kotlinx.serialization.Serializable
data class VideoOwner(
    val mid: Long,
    val name: String,
    val face: String
) {
    fun toHumanReadableString() = "UP主: $name(uid$mid)"
}
