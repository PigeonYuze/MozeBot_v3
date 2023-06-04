package com.pigeonyuze.command.phigros.data

@kotlinx.serialization.Serializable
data class PhigrosSongLevel(val chartAuthor: String, val hard: Int, val destiny: Double, val notes: Int) {
    fun toHumanReadableString() = "难度: $hard(${destiny}) 物量: $notes 谱师(Chart): $chartAuthor "

    enum class Level{
        AT,IN,HD,EZ,SP
    }
}

