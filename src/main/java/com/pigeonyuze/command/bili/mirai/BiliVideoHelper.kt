package com.pigeonyuze.command.bili.mirai

import com.pigeonyuze.MozeBotCore
import com.pigeonyuze.command.bili.data.BiliVideoCode
import com.pigeonyuze.command.bili.data.BiliVideoCode.*
import com.pigeonyuze.command.bili.data.BiliVideoInfo
import com.pigeonyuze.util.launch
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import net.mamoe.mirai.message.data.LightApp
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.FileOutputStream
import java.net.URLEncoder
import java.util.*

object BiliVideoHelper {
    private val picFile by lazy { MozeBotCore.resolveDataFile("bili/pic") }
    private const val videoUrl = "https://api.bilibili.com/x/web-interface/view"
    private const val LIGHT_APP_VIEW = "view_8C8E89B49BE609866298ADDFF2DBABA4"
    fun requestVideo(id: String): BiliVideoInfo {
        val response = if (id.startsWith("av", true)) {
            id.drop(2).toLongOrNull()?.run {
                httpRequest(videoUrl, mapOf("aid" to this.toString()))
            } ?: throw IllegalArgumentException("错误的 Av 号标注")
        } else httpRequest(videoUrl, mapOf("bvid" to id))
        if (response.code > 400) {
            throw IllegalStateException("Cannot get response message,code: ${response.code}")
        }
        val body0 = response.body?.string() ?: throw IllegalStateException("Cannot get response body!")
        val bodyInfo = Json.decodeFromString<BiliVideoUrlBody>(body0)
        when (BiliVideoCode.codeOf(bodyInfo.code)) {
            SUCCEED -> { /* Nothing to do*/ }
            REQUEST_ERROR -> throw IllegalStateException("请求错误, ${bodyInfo.message}")
            INSUFFICIENT_PERMISSION -> throw IllegalStateException("无访问权限, ${bodyInfo.message}")
            CANNOT_FIND_OBJECT -> throw IllegalArgumentException("找不到稿件, ${bodyInfo.message}")
            INVISIBLE_OBJECT -> throw IllegalStateException("稿件不可见, ${bodyInfo.message}")
            OBJECT_UNDER_REVIEW -> throw IllegalStateException("稿件审核中, ${bodyInfo.message}")
        }
        return bodyInfo.data
    }

    fun parseQBiliUrl(lightApp: LightApp): BiliVideoInfo? {
        val json = Json.parseToJsonElement(lightApp.content).jsonObject
        if (json["view"]?.jsonPrimitive?.content != LIGHT_APP_VIEW) return null
        val unjumpedUrl = json["meta"]!!.jsonObject["detail_1"]?.jsonObject!!["qqdocurl"]?.jsonPrimitive?.content
            ?: throw IllegalStateException("Cannot find 'qqdocurl' from json: '$json'")
        val response = httpRequest(unjumpedUrl,null) // always is 'b23.tv/xxx'
        when(response.code) {
            302 -> { /* jump to 'https://www.bilibili.com/video/BV' */
                val jumpedTarget = response.headers["Location"] ?: throw IllegalArgumentException("Cannot get jumped target url")
                val bvid = jumpedTarget.substringAfter("https://www.bilibili.com/video/").substringBefore("?")
                return requestVideo(bvid)
            }
            in 400..600 -> throw IllegalArgumentException("Response error (Response: $response)")
            else -> throw IllegalArgumentException("Undefined code: #${response.code}($response)")
        }
    }

    @Serializable
    data class BiliVideoUrlBody(
        val code: Int,
        val message: String,
        val ttl: Int,
        val data: BiliVideoInfo
    )


    fun downland(http: String, saveName: String, param: Map<String, String>? = null) {
        if (picFile.exists()) return
        launch(Dispatchers.IO,CoroutineStart.UNDISPATCHED) {
            httpRequest(http, param).body!!.byteStream().use {
                val fileOutputStream = FileOutputStream(saveName)
                fileOutputStream.write(it.readAllBytes())
                fileOutputStream.flush()
                fileOutputStream.close()
            }
        }
    }

    fun httpRequest(
        http: String,
        param: Map<String, String>?,
    ): Response {
        var httpUrl = http
        if (param != null) {
            val stringJoiner = StringJoiner("&", "$http?", "")
            for ((key, value) in param) {
                stringJoiner.add("${URLEncoder.encode(key, "utf-8")}=${URLEncoder.encode(value, "utf-8")}")
            }
            httpUrl = stringJoiner.toString()
        }
        val httpClient = OkHttpClient()
        val requestBody = Request.Builder()
            .get()
            .url(httpUrl)
            .build()
        return httpClient.newCall(requestBody).execute()
    }
}