package com.pigeonyuze.command.account.giftcode

import com.pigeonyuze.account.User
import com.pigeonyuze.account.UserManager.incrementCoin
import com.pigeonyuze.account.UserManager.incrementExp
import java.time.LocalDateTime

@kotlinx.serialization.Serializable
data class GiftCode(
    val code: String,
    val coins: Int,
    val exp: Long,
    val deadline0: com.pigeonyuze.util.data.LocalDateTime? = null,
    val initiator: Long,
    val maxUserNumber: Int = -1,
) {
    val deadline by lazy { deadline0?.toJLocalDateTime() }

    private val usedUserQQid = mutableListOf<Long>()
    var usedNum = 0
    var isCancel = false
        private set
    var isOverTime = false
        private set

    fun use(user: User) {
        if (isCancel) {
            throw IllegalGiftcodeException("Cannot use gift code(#$code),because it is canceled.")
        }
        if (deadline != null && LocalDateTime.now() > deadline) {
            cancel()
            isOverTime = true
            throw IllegalGiftcodeException("This gift code is closed(closed at $deadline)")
        }
        if (usedNum >= maxUserNumber && maxUserNumber != -1) {
            cancel()
            return
        }
        usedNum++
        usedUserQQid.add(user.qqId)
        user.incrementCoin(coins)
        user.incrementExp(exp)
    }

    fun isUserUsed(user: User): Boolean {
        return usedUserQQid.contains(user.qqId)
    }

    fun cancel() {
        if (isCancel) throw IllegalGiftcodeException("Cannot cancel gift code(#$code),because it is canceled.")
        isCancel = true
    }

}
