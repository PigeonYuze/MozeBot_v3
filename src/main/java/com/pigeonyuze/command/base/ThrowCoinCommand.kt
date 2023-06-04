package com.pigeonyuze.command.base

import com.pigeonyuze.MozeBotCore
import com.pigeonyuze.account.UserManager
import com.pigeonyuze.account.UserManager.incrementCoin
import com.pigeonyuze.util.fromQQ
import com.pigeonyuze.util.quote
import net.mamoe.mirai.console.command.CommandContext
import net.mamoe.mirai.console.command.SimpleCommand
import kotlin.random.Random

object ThrowCoinCommand : SimpleCommand(
    owner = MozeBotCore,
    primaryName = "throw",
    description = "抛一枚硬币，也许会有别样的收获"
) {
    private val unthrownCoin: ArrayList<String> = arrayListOf(
        "看向井里堆满了雨沫币，你想了一下，决定不抛雨沫币了",
        "突然下起雨了，你没有抛硬币",
        "诶，在你去往丢雨沫币的的时候，突然捡到了2个雨沫币\n于是你把两枚雨沫币拿去丢了，没有留下雨沫币"
    )

    private val lostCoin1 = arrayListOf(
        "你抛弃了一枚雨沫币，但是什么也没有发生\n雨沫币-1",
        ".......你抛下了那一枚硬币...\n\n(为什么要抛下它呢？......难道只要对别人没有用了就只会被这样无情地抛下吗。。。)\n雨沫币-1",
        "我不知道该写什么...反正雨沫币抛了就是了\n雨沫币-1",
        "你遇到了墨泽酱，墨泽酱竟然给了你10个雨沫币，并且劝你不要丢下雨沫币。你没有照做，全部丢了下去\n雨沫币-1",
        "哇！！！你抛下了一枚硬币，比旁边的人少抛了10个！！\n雨沫币-1",
        """
            你走到了池水边，正要掏出硬币。
            你摸着硬币那坚硬的外壳，你突然感受到有什么在吸引着你。
            好了，你轻轻地抚摸着这硬币，硬币上的"雨沫币"什么时候变得这么显眼了？一股坚硬而又温暖的感觉顺指尖传入身体......该死，硬币怎么还有如此的温度，你不理解，也许是手握着的缘故吧。
            你看着这枚硬币，仿佛感受到了它的心脏在与你共同跳动...见鬼，硬币怎么会有心跳？
            你想了许久，最终才想起来要把这枚硬币丢了。
            雨沫币-1
        """.trimIndent()
    )

    private val lostCoin2 = arrayListOf(
        "你在走向目的地的时候，不小心摔了一跤。\n又掉下去了一枚硬币...真是可惜啊...\n雨沫币-2",
        "在丢硬币的时候，不小心多丢了一枚硬币\n雨沫币-2",
        """
            java.lang.RunningException: Coin -2
                at MozeBotCore.mirai2.jar//command.base.ThrowCoinCommand.I_DO_NOT_THROW_IT(ThrowCoinCommand.kt:11)
                at MozeBotCore.mirai2.jar//command.base.ThrowCoinCommand.HEY_YOU_MUST_DO_IT(ThrowCoinCommand.kt:28)
                at MozeBotCore.mirai2.jar//command.base.ThrowCoinCommand.FUCK_YOU(ThrowCoinCommand.kt:54)
            雨沫币-2    
        """.trimIndent()
    )

    @Handler
    @Suppress("UNUSED")
    suspend fun handle(commandContext: CommandContext) = commandContext.run {
        val random = Random
        val range = random.nextInt(0, 1000)
        val user = UserManager.regUserOf(fromQQ)
        when {
            range < 50 -> {
                quote(
                    "你遇到了墨泽酱，墨泽酱竟然给了你10个雨沫币，并且劝你不要丢下雨沫币。你照做了。\n" +
                            "雨沫币+10"
                )
                user.incrementCoin(10)
            }
            range < 500 -> {
                quote(lostCoin1.random())
                user.incrementCoin(-1)
            }
            range < 700 -> {
                quote(lostCoin2.random())
                user.incrementCoin(-2)
            }
            range < 800 -> {
                quote("诶，在你去往丢雨沫币的的时候，突然捡到了2个雨沫币\n于是你把1枚拿去丢了，留下了一枚雨沫币\n雨沫币+1")
                user.incrementCoin(1)
            }
            range < 900 -> {
                quote(
                    "似乎是神明显灵了，在你抛下那一枚雨沫币的时候\n天空突然下起了大雨，从天空中飞来了三个雨沫币！\n雨沫币+2"
                )
                user.incrementCoin(2)
            }
            else -> {
                quote(unthrownCoin.random())
            }
        }
    }
}