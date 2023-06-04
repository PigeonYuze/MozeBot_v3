package com.pigeonyuze.command.phigros.data

import com.pigeonyuze.exception.AlreadyExistException
import com.pigeonyuze.exception.NoPermissionException
import net.mamoe.mirai.message.code.MiraiCode.deserializeMiraiCode

data class CommentsObject(
    val senderName: String,
    val senderID: Long,
    val miraiStringData: String,
    var like: Int,
    var bad: Int,
    var isQuote: Boolean,
    val id: Int,
) : Comparable<Any?> {
    private var isDeleted = false
    private val likeMembersList: MutableList<Long> = ArrayList()
    private val badMembersList: MutableList<Long> = ArrayList()
    val sendTime: Int = Math.toIntExact(System.currentTimeMillis() / 1000)
    var quoteID = -1

    @Throws(AlreadyExistException::class)
    fun like(likeMemberID: Long) {
        if (likeMembersList.contains(likeMemberID)) throw AlreadyExistException("You've already done it $likeMemberID")
        like++
        likeMembersList.add(likeMemberID)
    }

    @Throws(AlreadyExistException::class)
    fun bad(likeMemberID: Long) {
        if (badMembersList.contains(likeMemberID)) throw AlreadyExistException("You've already liked it")
        bad++
        badMembersList.add(likeMemberID)
    }

    @Throws(NoPermissionException::class)
    fun rm(rmMemberID: Long) {
        if (senderID != rmMemberID) throw NoPermissionException("The comment must be deleted by the sender or mozebot-TEST above permissions")
        likeMembersList.clear()
        like = -1
        isDeleted = true
    }

    @Throws(AlreadyExistException::class,NoPermissionException::class,IllegalArgumentException::class)
    fun setQuote(id: Int){
        if (!isQuote) throw NoPermissionException("It must be quote")
        if (quoteID != -1) throw AlreadyExistException("It has already been set up")
        if (quoteID > id) throw IllegalArgumentException("quoteID must > this.id")
        quoteID = id
    }

    override fun compareTo(other: Any?): Int {
        val commentsObject = other as CommentsObject
        return id + commentsObject.id
    }

    fun message() = deserializeMiraiCode(miraiStringData)
            .plus(
                "\n有 $like 个人感到赞同 | 有 $bad 个人选择了不喜欢 | 评论号：$id"
            )

}
