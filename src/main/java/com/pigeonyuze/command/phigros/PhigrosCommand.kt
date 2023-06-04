package com.pigeonyuze.command.phigros

import com.pigeonyuze.MozeBotCore
import com.pigeonyuze.command.phigros.data.PhigrosSongManager
import com.pigeonyuze.command.phigros.data.PhigrosSongUtils
import com.pigeonyuze.command.phigros.mirai.PhigrosGuessImage
import com.pigeonyuze.command.phigros.mirai.PhigrosRandomCommand
import com.pigeonyuze.util.from
import com.pigeonyuze.util.quote
import com.pigeonyuze.util.subject
import net.mamoe.mirai.console.command.CommandContext
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildForwardMessage
import net.mamoe.mirai.message.data.sendTo

@OptIn(ConsoleExperimentalApi::class)
object PhigrosCommand : CompositeCommand(
    MozeBotCore, "pgr",
) {


    @Suppress("UNUSED")
    @SubCommand("guessp", "guess png")
    @Description("开启一场图片竞猜")
    suspend fun guessPng(
        commandContext: CommandContext,
        @Name("图片长度") x: Int = 166,
        @Name("图片宽度") y: Int = 166,
    ) = PhigrosGuessImage.guessp(commandContext, x, y)

    @Suppress("UNUSED")
    @SubCommand("random")
    @Description("随机抽取曲目打法")
    suspend fun random(context: CommandContext) = context.run {
        quote(PhigrosRandomCommand.getRandomSongAndTechnique(subject))
    }

    @Suppress("UNUSED")
    @SubCommand("random song")
    @Description("随机抽取一首歌的详情")
    suspend fun randomSong(context: CommandContext) = context.run {
        quote(PhigrosSongManager.PHIGROS_SONG_LIST.random().toMiraiMessage(subject))
    }

    @Suppress("UNUSED")
    @SubCommand("song")
    @Description("使用歌名获取歌曲详情")
    suspend fun song(context: CommandContext, name: String) = context.run {
        val phigrosSongs = PhigrosSongManager.PHIGROS_SONG_LIST.filter {
            it.matchingEqual(name)
        }

        quote(when {
            phigrosSongs.size == 1 || phigrosSongs.any { it.masterName == name } -> phigrosSongs[0].toMiraiMessage(subject)
            phigrosSongs.size > 1 -> buildForwardMessage(subject) {
                from!! says "找到了多个匹配结果"
                phigrosSongs.forEach {
                    from!! says it.toMiraiMessage(subject)
                }
            }.copy(
                title = "Phigros 歌曲查询结果",
                preview = phigrosSongs.map { it.masterName }
            )
            else -> PlainText("没有找到该歌曲！")
        })
    }

    @Suppress("UNUSED")
    @SubCommand("find")
    @Description("寻找符合要求的歌曲")
    suspend fun find(context: CommandContext, category: Int, difficulty: Double, isStrictMatch: Boolean = false) =
        context.run {
            val phigrosSongs =
                PhigrosSongUtils.INSTANCE.querySongByDifficulty(category, difficulty, isStrictMatch)
            buildForwardMessage(subject) {
                phigrosSongs.forEach {
                    from!! says it.humanReadable()
                    from!! says it.illustrationToImage(subject)
                }
            }.copy(
                title = "Phigros 歌曲查询结果",
                preview = phigrosSongs.map { it.masterName }
            ).sendTo(subject)
        }

    @SubCommand
    @Description("获取指定章节的信息")
    suspend fun chapter(context: CommandContext,chapterName: String) = context.run {
        val phigrosSongs = PhigrosSongManager.CHAPTER_SONG_MAP[chapterName] ?: return@run
        buildForwardMessage(subject) {
            for (song in phigrosSongs) {
                from!! says PlainText(song.humanReadable()) + song.illustrationToImage(subject)
            }
        }.sendTo(subject)
    }
}