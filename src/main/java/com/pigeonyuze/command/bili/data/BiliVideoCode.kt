package com.pigeonyuze.command.bili.data

@kotlinx.serialization.Serializable
enum class BiliVideoCode(val code: Int) {
    /**
     * 成功
     * */
    SUCCEED(0),

    /**
     * 请求错误
     * */
    REQUEST_ERROR(-400),

    /**
     * 权限不足
     * */
    INSUFFICIENT_PERMISSION(-403),

    /**
     * 找不到对象
     * */
    CANNOT_FIND_OBJECT(-404),

    /**
     * 稿件不可见
     * */
    INVISIBLE_OBJECT(62002),

    /**
     * 稿件审核中
     * */
    OBJECT_UNDER_REVIEW(62004);

    companion object {
        fun codeOf(objCode: Int) = BiliVideoCode.values().firstOrNull { it.code == objCode }
            ?: throw IllegalArgumentException("Unknown code of #$objCode")
    }

}