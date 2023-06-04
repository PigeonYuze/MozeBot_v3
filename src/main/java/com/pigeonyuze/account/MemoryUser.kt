package com.pigeonyuze.account

import com.pigeonyuze.util.launch
import kotlinx.coroutines.Dispatchers


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
class MemoryUser(
    override val uid: Int,
    override var canFreeSetName: Boolean,
    override var coin: Int,
    override var exp: Long,
    override var level: Int,
    override val qqId: Long,
    override val regDate: String,
    override var userName: String
) : User {
    private var physicalUser: PhysicalUser? = null
    private var saved = false

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

    /**
     * For writing
     * */
    override fun toString(): String {
        return "$uid,$userName,$coin,$exp,$qqId,$level,$canFreeSetName"
    }

    fun reg() {
        if (data.size > uid - 10000) return
        /* I/O to physical disk */
        launch(Dispatchers.IO) {
            PhysicalUser.USER_SAVE_FILE.outputStream().use {
                it.write(toString().encodeToByteArray())
                it.write('\n'.code)
            }
        }
    }

    fun mappingToData() {
        if (saved) {
            return
        }
        saved = true
        data.add(this)
    }

    /**
     * 将所映射的 [PhysicalUser] 的数据与当前数据进行同步
     *
     * 当不存在时，同时进行注册
     * */
    fun sync() {
        if (physicalUser == null) {
            reg()
            physicalUser = PhysicalUser(uid, uid - MEMORY_USER_MAPPING_VALUE)
        }
        if (level != ExperienceUtils.getLevel(exp)) {
            level = ExperienceUtils.getLevel(exp)
        }
        physicalUser!!.canFreeSetName = canFreeSetName
        physicalUser!!.coin = coin
        physicalUser!!.exp = exp
        physicalUser!!.level = level
        physicalUser!!.userName = userName
    }

    companion object {
        const val MEMORY_USER_MAPPING_VALUE = 9998
        val data = arrayListOf<MemoryUser>()
    }
}