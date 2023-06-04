package com.pigeonyuze.command.account.giftcode

import com.pigeonyuze.MozeBotCore
import com.pigeonyuze.account.UserManager.regUserOf
import com.pigeonyuze.util.GiftCodeData
import com.pigeonyuze.util.data.LocalDateTime.Companion.toKLocalDateTime
import com.pigeonyuze.util.fromQQ
import com.pigeonyuze.util.quote
import net.mamoe.mirai.console.command.CommandContext
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.permission.PermissionService.Companion.hasPermission
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.PermissionDeniedException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object GiftCodeCommand : CompositeCommand(
    MozeBotCore, "code"
) {
    @Suppress("UNUSED")
    @SubCommand("use")
    suspend fun use(commandContext: CommandContext, code: String) = commandContext.run handle@{
        val giftCode = GiftCodeManager.of(code) ?: kotlin.run {
            quote("无法找到该兑换码！")
            return@handle
        }
        val user = sender.regUserOf()
        if (giftCode.isUserUsed(user)) {
            quote("你已经使用过该兑换码了哦~")
            return@handle
        }
        giftCode.use(user)
        quote("已成功使用该来自兑换码！\n获取了 ${giftCode.coins} 个雨沫币，增长了 ${giftCode.exp} 经验值\n该兑换码截止于 ${giftCode.deadline}！")
    }

    @OptIn(ConsoleExperimentalApi::class)
    @Suppress("UNUSED")
    @SubCommand("create")
    suspend fun create(
        commandContext: CommandContext,
        coins: Int,
        exp: Long,
        @Name("截止日期(遵循`MM-dd HH:mm:ss`)") deadLineDescribe: String? = null,
        maxUserNumber: Int = -1,
    ) = commandContext.run {
        if (!sender.hasPermission(MozeBotCore.adminPermission)) {
            throw PermissionDeniedException("Cannot run create command,the permission of sender must be ADMIN")
        }
        val deadLine = if (deadLineDescribe != null) LocalDateTime.parse(
            deadLineDescribe,
            DateTimeFormatter.ofPattern("MM-dd HH:mm:ss")
        )
        else LocalDateTime.MAX
        val giftCode = GiftCode(
            "moze-gift-${RandomTextManager.createBigSmallLetterStrOrNumberRandom(12)}",
            coins,
            exp,
            deadLine.toKLocalDateTime(),
            fromQQ,
            maxUserNumber
        )
        GiftCodeData.giftCodes.add(giftCode)
        quote("已成功创建兑换码\nInfo Message:\n$giftCode")
    }
}