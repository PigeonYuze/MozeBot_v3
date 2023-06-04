package com.pigeonyuze.util

import com.pigeonyuze.MozeBotCore.reload
import com.pigeonyuze.command.account.SignCommand
import com.pigeonyuze.command.account.giftcode.GiftCode
import com.pigeonyuze.command.account.redpacket.RedPacket.RedPackets
import com.pigeonyuze.command.recreation.FortuneCommand
import com.pigeonyuze.util.DailyRefreshTasks.Refresh
import kotlinx.serialization.Serializable
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value


object BotSendMessageConfig : AutoSavePluginConfig("bot_sendMessage") {
    var isOnlySendToGroup by value(false)

    var openMessageShielding by value(true)

    val defMaxSentCount by value(
        mutableMapOf<Long /* Target Id */, Int /* Max Sending Message count */>()
    )
}

object BotSetting : AutoSavePluginConfig("bot_setting") {
    val authorisedGroupId: MutableSet<Long> by value(mutableSetOf())
    val pushGroupId: MutableSet<Long> by value(mutableSetOf())
    val authorisedFriendId: MutableSet<Long> by value(mutableSetOf())
    val masterDebugGroupId: Long by value(114514L)
    val blackGroupList: MutableSet<Long> by value(mutableSetOf())
}

object UnopenedRunningData : AutoSavePluginData("__unopened_running_info"), DailyRefreshTasks {
    val friendCode: MutableSet<FriendCode> by value(mutableSetOf())

    @JvmStatic
    @Refresh
    var randomPantBestUid: Pair<Int,Int> by value(0 to 0)

    @Refresh
    var randomDickBestData: Pair<String,Double> by value("root" to -21.99)

    @Refresh
    var signData: MutableList<SignCommand.SignData> by value(mutableListOf())

    @Refresh
    var fortuneData: MutableList<FortuneCommand.FortuneInfo> by value(mutableListOf())

    init {
        initRefresh()
    }

    @Serializable
    data class FriendCode(
        val obj: Long,
        val code: String,
    )
}

object GiftCodeData : AutoSavePluginData("gift_code") {
    val giftCodes: MutableList<GiftCode> by value(mutableListOf())
    @property:JvmStatic
    val redPacketsList: MutableList<RedPackets> by value(ArrayList())
    @property:JvmStatic
    val textRedPackets: MutableMap<String, RedPackets> by value(HashMap())
}

fun reload() {
    BotSendMessageConfig.reload()
    UnopenedRunningData.reload()
}