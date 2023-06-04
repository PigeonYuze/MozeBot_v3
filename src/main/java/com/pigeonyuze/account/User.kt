package com.pigeonyuze.account

/**
 * ### 用户的实现
 * */
interface User{
    val uid: Int
    val qqId: Long
    val regDate: String
    var coin: Int
    var exp: Long
    var level: Int
    var userName: String
    var canFreeSetName: Boolean
}
