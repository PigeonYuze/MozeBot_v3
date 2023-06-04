package com.pigeonyuze.command.account.giftcode

import com.pigeonyuze.account.User
import com.pigeonyuze.util.GiftCodeData.giftCodes

object GiftCodeManager {
    fun add(giftCode: GiftCode) {
        giftCodes.add(giftCode)
    }
    fun use(code: String,user: User) : Boolean{
        val giftCode = giftCodes.firstOrNull {
            !it.isCancel && it.code == code
        } ?: return false
        giftCode.use(user)
        return true
    }
    fun of(code: String) = giftCodes.firstOrNull { it.code == code }
    fun rm(code: String) {
        val iterator = giftCodes.iterator()
        while (iterator.hasNext()) {
            val value = iterator.next()
            if (code == value.code || value.isCancel) {
                iterator.remove()
            }
        }
    }
}