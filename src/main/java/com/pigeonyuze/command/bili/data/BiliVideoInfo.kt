package com.pigeonyuze.command.bili.data

import com.pigeonyuze.command.bili.mirai.BiliVideoHelper
import com.pigeonyuze.util.dateFormatter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Serializable
data class BiliVideoInfo(
    val bvid: String,
    val aid: Long,
    @SerialName("videos")
    val videoNum: Int,
    @SerialName("tid")
    val partitionTid: Int,
    @SerialName("tname")
    val partitionName: String,
    @SerialName("copyright")
    val copyright0: Int,
    val pic: String,
    val title: String,
    @SerialName("pubdate")
    val pushTime: Int,
    val ctime: Int,
    val desc: String,
    @SerialName("desc_v2")
    val decs2: ArrayList<BiliVideoDescV2>,
    @SerialName("state")
    val state0: Int,
    @SerialName("duration")
    val durationOfSecond: Long,
    val forward: Long = 0,
    @SerialName("mission_id")
    val missionId: Int,
    @SerialName("redirect_url")
    val redirectUrl: String,
    val owner: VideoOwner,
    val stat: VideoStat,
    val dynamic: String,
    val pages: List<VideoPage>
    // And so on...
) {
    val copyright by lazy { copyright0 == 1}
    val state by lazy { VideoState.codeOf(state0) }
    val pushDateTime: LocalDateTime by lazy {
        LocalDateTime.ofInstant(Instant.ofEpochMilli(pushTime * 1000L), ZoneId.systemDefault())
    }

    suspend fun toMiraiMessage(subject: Contact) = buildMessageChain {
        +toHamanReadableString()
        File(pic).toExternalResource().use {
            +subject.uploadImage(it)
        }
    }

    fun downlandPic(): BiliVideoInfo {
        BiliVideoHelper.downland(pic, bvid)
        return this
    }

    fun toHamanReadableString() = """
        $title
        $bvid(av$aid)
        ${stat.argue_msg}
        播放数: $videoNum 分区: $partitionName
        ${stat.toHamanReadableString()}
        视频上次时间: ${pushDateTime.format(dateFormatter)}
        ${owner.toHumanReadableString()}${if (pages.isEmpty()) "" else "\n共 ${pages.size} 个分集"}
        简介:
        ${descToString()}
    """.trimIndent()

    private fun descToString(): String {
        if (desc.length < 50) return desc
        return desc.substring(0,50) + "..."
    }

    enum class VideoState(val code: Int,val desc: String) {
        ORANGE_PASSED(1,"橙色通过"),
        OPEN(0,"开放浏览"),
        UNREVIEWED(-1,"待审核"),
        FAILED_REVIEW(-2,"被打回"),
        POLICE_LOCKED(-3,"网警锁定"),
        DUPLICATE_VIDEO_LOCKED(-4,"视频撞车了"),
        ADMIN_LOCKED(-5,"管理员锁定"),
        FIX_WAITING_REVIEW(-6,"修复待审"),
        RESPITE_REVIEW(-7,"暂缓审核"),
        NEW_VIDEO_UNREVIEWED(-8,"补档待审"),
        WAIT_FOR_ENCODING(-9,"等待编码"),
        DELAY_REVIEW(-10,"延迟审核"),
        ERROR_VIDEO_SOURCE(-11,"视频源待修"),
        FAILED_DUMP(-12,"转储失败"),
        ALLOW_COMMENT_REVIEWED(-13,"允许评论待审"),
        TMP_RECYCLE_BIN(-14,"临时回收站"),
        DISTRIBUTING(-15,"分发中"),
        FAILED_ENCODING(-16,"编码失败"),
        UNCOMMITTED_CREATE_REQUEST(-20,"创建未提交"),
        COMMITTED_CREATE_REQUEST(-30,"创建已提交"),
        TIMED_SENT(-40,"延迟发送"),
        USER_DELETED(-100,"用户删除");
        companion object {
            fun codeOf(objCode: Int) = VideoState.values().firstOrNull { it.code == objCode} ?: throw IllegalArgumentException("Unknown code #$objCode")
        }
    }
}
