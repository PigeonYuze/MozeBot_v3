package com.pigeonyuze.account

import com.pigeonyuze.MozeBotCore
import com.pigeonyuze.util.launch
import kotlinx.coroutines.Dispatchers
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.FileInputStream
import java.io.InputStreamReader

/**
 * 较为底层的 [User] 实现
 *
 * - 对数据的读取修改直接映射到`I/O`流，因此每一次的值都将是最新的。
 * - 信息直接存储在磁盘上
 * - 每一次变更直接作用于物理磁盘
 *
 * **每一次操作都将映射至系统`I/O`层，这大概率会影响性能，一般的，请使用 [MemoryUser] 作为后续流操作**
 *
 * @suppress 此处的 [uid] 必须为已经注册，能在文件中找到的具体数据
 *
 * @see MemoryUser
 * */
class PhysicalUser(
    override val uid: Int,
    private var ofFileLine: Int = -1,
    private val readLine: String = ""
) : User {
    private val readiedElementIndex = mutableListOf<Int>()
    private var isReadiedLine = false
    private lateinit var memoryUser0: MemoryUser

    constructor(uid: Int, memoryUser: MemoryUser) : this(uid) {
        this.memoryUser0 = memoryUser
    }

    fun write(writer: BufferedWriter,lineNumber: Int = -1): Boolean {
        if (lineNumber != ofFileLine) {
            return false
        }
        if (!::memoryUser0.isInitialized) {
            memoryUser0 = MemoryUser(this)
        }
        val content = buildString {
            append(memoryUser0.userName)
            append(delimiter)
            append(memoryUser0.coin)
            append(delimiter)
            append(memoryUser0.regDate)
            append(delimiter)
            append(memoryUser0.exp)
            append(delimiter)
            append(memoryUser0.qqId)
            append(delimiter)
            append(memoryUser0.level)
            append(delimiter)
            append(memoryUser0.canFreeSetName)
        }
        with(writer) {
            write(content)
            write('\n'.code)
        }
        return true
    }

    override var canFreeSetName: Boolean
        get() = updateByIndex(IS_UNSEATED_NAME_INDEX).toBoolean()
        set(_) { throw NotImplementedError("Cannot write data from PhysicalUser") }

    override var coin: Int
        get() = updateByIndex(COIN_INDEX).toInt()
        set(_) { throw NotImplementedError("Cannot write data from PhysicalUser") }

    override var exp: Long
        get() = updateByIndex(USER_EXPERIENCE_INDEX).toLong()
        set(_) { throw NotImplementedError("Cannot write data from PhysicalUser") }


    override var level: Int
        get() = updateByIndex(LEVEL_INDEX).toInt()
        set(_) { throw NotImplementedError("Cannot write data from PhysicalUser") }


    override val qqId: Long
        by lazy { updateByIndex(QQ_ID_INDEX).toLong() }

    override val regDate: String
        get() = updateByIndex(REG_TIME_INDEX)

    override var userName: String
        get() = updateByIndex(USER_NAME_INDEX)
        set(_) { throw NotImplementedError("Cannot write data from PhysicalUser") }

    private fun updateByIndex(index: Int) : String{
        if (!readiedElementIndex.contains(index) && readLine.isNotEmpty()) {
            readiedElementIndex.add(index)
            isReadiedLine = false
        }
        val update = update()
        return update.getOrNull(index) ?: kotlin.run {
            throw IndexOutOfBoundsException("$update doesn't have index#$index!")
        }
    }
    
    private fun update() : List<String> {
        if (!isReadiedLine && readLine.isNotEmpty()) {
            return readLine.split(delimiter)
        }
        val inputStream = BufferedReader(InputStreamReader(FileInputStream(USER_SAVE_FILE), Charsets.UTF_8))
        var lineData = ""
        inputStream.use {
            var i = 0
            while (inputStream.readLine()?.also { lineData = it } != null) {
                i++
                if (lineData.substringBefore(delimiter).toInt() != uid) {
                    continue
                }
                break
            }
        }
        return lineData.split(delimiter)
    }

    companion object {
        val USER_SAVE_FILE = MozeBotCore.resolveDataFile("user.txt")
        val USER_SAVE_FILE_TMP = MozeBotCore.resolveDataFile("user_lasted.txt")
        init {
            launch(Dispatchers.IO) {
                USER_SAVE_FILE.createNewFile()
            }
        }

        fun PhysicalUser.asMemoryUser(): MemoryUser {
            if (!::memoryUser0.isInitialized) {
                memoryUser0 = MemoryUser(this)
            }
            return memoryUser0
        }

        private const val delimiter = ","
        private const val USER_NAME_INDEX = 0
        private const val COIN_INDEX = 1
        private const val REG_TIME_INDEX = 2
        private const val USER_EXPERIENCE_INDEX = 3
        private const val QQ_ID_INDEX = 4
        private const val LEVEL_INDEX = 5
        private const val IS_UNSEATED_NAME_INDEX = 6
    }
}
