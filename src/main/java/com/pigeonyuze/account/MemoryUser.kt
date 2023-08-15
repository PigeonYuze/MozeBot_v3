package com.pigeonyuze.account

import com.pigeonyuze.account.User.Companion.ofLevel
import java.io.FileOutputStream


/**
 * 相较予 [PhysicalUser] 的另一种用户实现
 *
 * - 用户数据的修改都只映射到内存上，不进行`I/O`流
 * - 存储载体处于内存
 * - 与 [PhysicalUser] 映射
 * - 可用于注册新的用户
 *
 * @see PhysicalUser
 * */
data class MemoryUser(
    override val uid: Int,
    override var canFreeSetName: Boolean,
    override var coin: Int,
    override var exp: Long,
    private val level0: Int,
    override val qqId: Long,
    override val regDate: String,
    override var userName: String
) : User {
    var physicalUser: PhysicalUser? = null
        private set
    @Suppress("SuspiciousVarProperty")
    override var level: Int = level0
        get() = exp.ofLevel()

    constructor(physicalUser: PhysicalUser) : this(
        physicalUser.uid,
        physicalUser.canFreeSetName,
        physicalUser.coin,
        physicalUser.exp,
        physicalUser.level,
        physicalUser.qqId,
        physicalUser.regDate,
        physicalUser.userName
    ) {
        this.physicalUser = physicalUser
    }

    init {
        data.add(this)
    }

    fun reg() {
        if (physicalUser != null)
            return
        FileOutputStream(PhysicalUser.USER_SAVE_FILE,true).use {
            with(PhysicalUser(uid,this)) {
                physicalUser = this
                write(it.bufferedWriter(Charsets.UTF_8),-1)
            }

        }

    }

    companion object {
        const val MEMORY_USER_MAPPING_VALUE = 10000
        val data = arrayListOf<MemoryUser>()
    }
}