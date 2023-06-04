package com.pigeonyuze.command.recreation

import com.pigeonyuze.MozeBotCore
import com.pigeonyuze.util.*
import com.pigeonyuze.util.resourcesFileOf
import kotlinx.serialization.Serializable
import net.mamoe.mirai.console.command.CommandContext
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import java.math.BigDecimal
import kotlin.random.Random

object FortuneCommand: SimpleCommand(
    MozeBotCore, "fortune"
) {
    private val unluckyFile by lazy { resourcesFileOf("fortune/bad.jpg") }
    private val veryUnluckyFile by lazy { resourcesFileOf("fortune/big bad.jpg") }
    private val mediumUnluckyFile by lazy { resourcesFileOf("fortune/not big not small bad.jpg") }
    private val medium by lazy { resourcesFileOf("fortune/ping.jpg") }
    private val luckyFile by lazy { resourcesFileOf("fortune/luck.jpg") }
    private val veryLuckyFile by lazy { resourcesFileOf("fortune/big luck.jpg") }
    private val mediumLuckyFile by lazy { resourcesFileOf("fortune/not big not small luck.jpg") }
    private val aLittleLuckyFile by lazy { resourcesFileOf("fortune/small luck.jpg") }

    @Handler
    suspend fun handle(commandContext: CommandContext) = commandContext.run {
        val fortuneData = UnopenedRunningData.fortuneData.firstOrNull { it.userQQid == fromQQ} ?:
            FortuneInfo(
                fromQQ,
                Random.nextInt(0,1000),
                Random.nextInt(0,15)
            )
        val fortuneImageFile = when(fortuneData.root) {
            in 0..50 -> veryUnluckyFile
            in 50..250 -> unluckyFile
            in 250..400 -> mediumUnluckyFile
            in 400..550 -> medium
            in 550..650 -> aLittleLuckyFile
            else -> {
                when(fortuneData.nested) {
                    in 0..6 -> luckyFile
                    in 6..13 -> mediumLuckyFile
                    else -> veryLuckyFile
                }
            }
        }
        val out = BigDecimal(fortuneData.root + fortuneData.nested).divide(BigDecimal.TEN).toInt()
        fortuneImageFile.toExternalResource().use {
            sendMessageChain {
                +"您今日的运势值为: $out!"
                +uploadImage(it)
            }
        }
    }

    @Serializable
    data class FortuneInfo(
        val userQQid: Long,
        val root: Int,
        val nested: Int,
    )

}