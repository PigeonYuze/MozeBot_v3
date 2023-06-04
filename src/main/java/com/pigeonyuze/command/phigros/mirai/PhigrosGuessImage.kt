package com.pigeonyuze.command.phigros.mirai

import com.pigeonyuze.command.phigros.data.PhigrosSong
import com.pigeonyuze.command.phigros.data.PhigrosSongManager
import com.pigeonyuze.account.UserManager
import com.pigeonyuze.account.UserManager.incrementCoin
import com.pigeonyuze.account.UserManager.incrementExp
import com.pigeonyuze.account.UserManager.regUserOf
import com.pigeonyuze.util.*
import kotlinx.coroutines.withTimeoutOrNull
import net.mamoe.mirai.console.command.CommandContext
import net.mamoe.mirai.event.EventPriority
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.syncFromEvent
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import java.awt.Rectangle
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.imageio.ImageIO

object PhigrosGuessImage {
    private val useGuessGroupData: MutableMap<Long, PhigrosSong> = Collections.synchronizedMap(HashMap())

    suspend fun guessp(commandContext: CommandContext, x: Int = 166, y: Int = 166) = commandContext.run {
        if (checkGroupIsGuessing(fromGroup)) {
            return
        }
        sender.regUserOf().incrementCoin(-1)
        val phigrosSong = createGuessProblem(this.fromGroup, x, y)
        if (phigrosSong === PhigrosSong.nullObject || phigrosSong == null) {
            quote("该群聊已经开始了一场竞猜 或 程序出现了意想不到的错误")
            return
        }

        File("D:\\phigros\\Illustration\\Illustration\\guess\\" + this.fromGroup + ".png").toExternalResource("png")
            .use {
                quote(
                    PlainText("Phigros - Image竞猜\n30秒后将自动停止\n题目图片: ") + uploadImage(it)
                )
            }
        checkAnswer<MessageEvent>(phigrosSong)
    }

    private suspend inline fun <reified P : MessageEvent> CommandContext.checkAnswer(
        phigrosSong: PhigrosSong,
    ) {
        /* message filter */
        val filter: P.(P) -> Boolean = {
            if (it.message.findIsInstance<At>()?.target == this.bot.id) {
                println(it.message.contentToString())
                phigrosSong.matchingEqual(it.message.findIsInstance<PlainText>()?.contentToString() ?: "null")
            } else false
        }
        val mapper: suspend (P) -> P? = mapper@{ event ->
            if (event.subject != this.subject) return@mapper null
            if (!filter(event, event)) return@mapper null
            event
        }
        val answer: P? = try {
            withTimeoutOrNull(30_000) {
                GlobalEventChannel.syncFromEvent(EventPriority.NORMAL, mapper)
            }
        } catch (e: Exception) {
            loggingError(e)
            subject.sendMessage("发生了意想不到的错误,已停止(已归还原扣除的coin)")
            useGuessGroupData.remove(fromContact)
            UserManager.userOf(fromQQ)?.incrementCoin(1)
            return
        }

        if (answer === null) {
            buildMessageChain {
                +originalMessage.quote()
                +"竞猜结束了哦！\n本次竞猜的答案是: \n${phigrosSong.masterName}"
                File(phigrosSong.illustrationPath).toExternalResource().use {
                    +subject.uploadImage(it)
                }
            }.sendTo(subject)
        } else {
            subject.sendMessage(answer.message.quote().plus("恭喜你猜对了哟！\n获得奖励 5 个雨沫币和 50 经验值！"))
            val user = UserManager.userOf(answer.sender.id)
            user?.incrementExp(50L)
            user?.incrementCoin(5)
        }
        synchronized(useGuessGroupData) {
            useGuessGroupData.remove(fromContact)
        }

    }

    private fun checkGroupIsGuessing(groupId: Long): Boolean =
        synchronized(useGuessGroupData) {
            return useGuessGroupData.containsKey(groupId)
        }

    private fun createGuessProblem(groupId: Long, objectW: Int = 166, objectY: Int = 166): PhigrosSong? =
        synchronized(useGuessGroupData) {
            val var2: Iterator<Long> = useGuessGroupData.keys.iterator()
            var groupIdOfMap: Long
            if (!var2.hasNext()) {
                val phigrosSong = PhigrosSongManager.randomSong()
                useGuessGroupData[groupId] = phigrosSong
                createGuessImage(groupId, phigrosSong, objectW, objectY)
                return phigrosSong
            }
            groupIdOfMap = var2.next()
            while (groupIdOfMap != groupId) {
                if (!var2.hasNext()) {
                    val phigrosSong = PhigrosSongManager.randomSong()
                    useGuessGroupData[groupId] = phigrosSong
                    createGuessImage(groupId, phigrosSong, objectW, objectY)
                    return phigrosSong
                }
                groupIdOfMap = var2.next()
            }
            return null
        }

    @Throws(IOException::class)
    private fun randomCutPNGImage(input: InputStream, out: OutputStream, objectW: Int = 166, objectY: Int = 166) {
        val imageStream = ImageIO.createImageInputStream(input)
        imageStream.use {
            val readers = ImageIO.getImageReadersByFormatName("png")
            val reader = readers.next()
            reader.setInput(imageStream, true)
            val param = reader.defaultReadParam
            val random = Random()
            val tempX = reader.getWidth(0)
            val tempY = reader.getHeight(0)
            val x = random.nextInt(if (tempX > objectW) tempX - objectW else tempX)
            val y = random.nextInt(if (tempY > objectY) tempY - objectY else tempY)
            val rect = Rectangle(x, y, objectW, objectY)
            param.sourceRegion = rect
            val bi = reader.read(0, param)
            ImageIO.write(bi, "png", out)
        }
    }

    private fun createGuessImage(groupId: Long, phigrosSong: PhigrosSong, objectW: Int = 166, objectY: Int = 166) {
        try {
            randomCutPNGImage(
                File(phigrosSong.illustrationPath).inputStream(), Files.newOutputStream(
                    Paths.get(
                        "D:\\phigros\\Illustration\\Illustration\\guess\\$groupId.png"
                    )
                ), objectW, objectY
            )
        } catch (e: IOException) {
            loggingError(e)
        }
    }
}