package com.pigeonyuze.command.account.giftcode

class IllegalGiftcodeException : Throwable{
    constructor(message: String?) : super(message)
    constructor(message: String?,cause: Throwable) : super(message, cause)
}