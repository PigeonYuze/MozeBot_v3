package com.pigeonyuze.account

import com.pigeonyuze.MozeBotCore
import com.pigeonyuze.util.launch
import com.pigeonyuze.util.loggingInfo
import kotlinx.coroutines.*
import java.io.*

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
    private var ofFileLine: Int = 0,
    private val readLine: String = ""
) : User {
    private val readiedElementIndex = mutableListOf<Int>()
    private var isReadiedLine = false
    
    override var canFreeSetName: Boolean
        get() = updateByIndex(IS_UNSEATED_NAME_INDEX).toBoolean()
        set(value) {
            write(IS_UNSEATED_NAME_INDEX, value.toString())
        }

    override var coin: Int
        get() = updateByIndex(COIN_INDEX).toInt()
        set(value) {
            write(COIN_INDEX,value.toString())
        }

    override var exp: Long
        get() = updateByIndex(USER_EXPERIENCE_INDEX).toLong()
        set(value) {
            write(USER_EXPERIENCE_INDEX, value.toString())
        }

    override var level: Int
        get() = updateByIndex(LEVEL_INDEX).toInt()
        set(value) {
            write(LEVEL_INDEX, value.toString())
        }

    override val qqId: Long
        by lazy { updateByIndex(QQ_ID_INDEX).toLong() }

    override val regDate: String
        get() = updateByIndex(REG_TIME_INDEX)

    override var userName: String
        get() = updateByIndex(USER_NAME_INDEX)
        set(value) {
            write(USER_NAME_INDEX,value)
        }

    private fun updateByIndex(index: Int) : String{
        if (!readiedElementIndex.contains(index) && readLine.isNotEmpty()) {
            readiedElementIndex.add(index)
            isReadiedLine = false
        }
        return update().getOrNull(index) ?: kotlin.run {
            throw IndexOutOfBoundsException("${update()} doesn't have index#$index!")
        }
    }
    
    private fun update() : List<String> {
        if (!isReadiedLine && readLine.isNotEmpty()) {
            return readLine.split(",")
        }
        val inputStream = BufferedReader(InputStreamReader(FileInputStream(USER_SAVE_FILE), Charsets.UTF_8))
        var lineData = ""
        inputStream.use {
            var i = 0
            while (inputStream.readLine()?.also { lineData = it } != null) {
                i++
                if (lineData.substringBefore(",").toInt() != uid) {
                    continue
                }
                break
            }
        }
        return lineData.split(",")
    }

    private fun write(index: Int,value: String) {
        val mutable = update().toMutableList()
        if (mutable[index] == value) return
        mutable[index] = value
        writeImpl(ofFileLine, mutable.joinToString())
    }

    companion object {
        val USER_SAVE_FILE = MozeBotCore.resolveDataFile("user.txt")

        /**
         * 在收到初始请求后，等待 `2s` 收集可能含有的所有请求
         * */
        private fun writeImpl(lineNumber: Int, newLineContent: String) {
            writeRequests[lineNumber] = newLineContent
            request()
        }

        private var isRequested = false
        /**
         * 等待指定时间后，执行写入工作
         * */
        private fun request() {
            if (isRequested) return
            isRequested = true
            runBlocking(Dispatchers.IO) {
                loggingInfo("Data Writer","Starting delay.")
                delay(1_300)
                loggingInfo("Data Writer","Delay stopped,start writing data")
                val newIndexToLineMap = writeRequests.toSortedMap()

                USER_SAVE_FILE.bufferedReader().useLines { lines ->
                    lines.forEachIndexed { index, line ->
                        USER_SAVE_FILE.bufferedWriter().use { output ->
                            if (newIndexToLineMap.containsKey(index)) {
                                output.write(newIndexToLineMap[index]!!)
                                output.newLine()
                            }else {
                                output.write(line)
                                output.newLine()
                            }
                        }
                    }
                }
                isRequested = false
            }


        }

        private val writeRequests = hashMapOf<Int,String>()

        init {
            launch(Dispatchers.IO) {
                USER_SAVE_FILE.createNewFile()
            }
        }

        fun PhysicalUser.asMemoryUser() = MemoryUser(this)

        private const val USER_NAME_INDEX = 1
        private const val COIN_INDEX = 2
        private const val REG_TIME_INDEX = 3
        private const val USER_EXPERIENCE_INDEX = 4
        private const val QQ_ID_INDEX = 5
        private const val LEVEL_INDEX = 6
        private const val IS_UNSEATED_NAME_INDEX = 7
    }
}
