package com.pigeonyuze.command.recreation.random

import com.pigeonyuze.MozeBotCore
import com.pigeonyuze.command.recreation.random.pants.RandomPc
import com.pigeonyuze.account.UserManager
import com.pigeonyuze.account.UserManager.incrementCoin
import com.pigeonyuze.util.*
import net.mamoe.mirai.console.command.CommandContext
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.data.UserProfile
import net.mamoe.mirai.message.data.MessageChainBuilder
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.random.Random

object BaseRandomCommand : CompositeCommand(
    MozeBotCore, "random"
) {
    @Suppress("UNUSED")
    @SubCommand("pc")
    @Description("随机获取当前胖次")
    suspend fun randomPant(context: CommandContext) = context.run {
        quote(RandomPc.getResult(fromQQ))
    }

    @Suppress("UNUSED")
    @SubCommand("dick")
    @Description("随机获取当前几把长度")
    suspend fun randomDick(context: CommandContext) = context.run {
        val random = Random
        if (random.nextInt(10) < 2) {
            quote("您当前没有哦~~\n杂鱼~杂鱼~")
            return@run
        }
        val dickData = BigDecimal(random.nextDouble(0.00, 22.00)).setScale(2,RoundingMode.HALF_EVEN).toDouble()
        val sex = from?.queryProfile()?.sex
        val best = UnopenedRunningData.randomDickBestData
        if (sex == UserProfile.Sex.FEMALE) {
            sendMessageChain {
                +"喂！！！女孩子到底是要怎么测啊？...测义肢吗？\n"
                +"您的义肢长度为: $dickData cm!\n"
                if (dickData < 6) {
                    +"就连义肢也这么杂鱼吗？杂鱼~杂鱼~"
                }
                compare(dickData, best, from?.nick ?: "Console")
            }
            return@run
        }
        if (dickData > 21.75) {
            UserManager.userOf(fromQQ)?.incrementCoin(3)
        }
        sendMessageChain {
            +"您当前的长度为: $dickData cm!\n"
            compare(dickData, best, from?.nick ?: "Console")
        }
    }

    private fun MessageChainBuilder.compare(
        dickData: Double,
        best: Pair<String, Double>,
        nick: String,
    ) {
        if (dickData > best.second) {
            +"大于了今日原最长记录"
            +"(${best.second}cm) "
            +"${BigDecimal(dickData).minus(BigDecimal(best.second)).setScale(2,RoundingMode.HALF_EVEN)}cm !"
            +"\n已自动更新数据"
            +"原"
            UnopenedRunningData.randomDickBestData = nick to dickData
        } else if (dickData < best.second) {
            +"小于了今日最长记录"
            +"(${best.second}cm) "
            +"${BigDecimal(best.second).minus(BigDecimal(dickData)).setScale(2,RoundingMode.HALF_EVEN)}cm !\n"
        } else {
            +"与今日最高记录相同!\n"
        }
        +"记录创造者: ${best.first}"
    }
}