package com.pigeonyuze.command.recreation.morse

import java.io.IOException
import java.util.*

class MorseConvertor {
    private val dotID = 0
    private val dashID = 1
    private val inspID = 2
    private val lspID = 3
    private val wspID = 4


    private val DOT = "·"
    private val DASH = "\u2014"
    private val INSP = " "
    private val LSP = "   "
    private val WSP = "     "

    private val morseMap: HashMap<Char, Array<Int>> = object : HashMap<Char, Array<Int>>() {
        init {
            this['A'] = arrayOf(
                dotID, inspID,
                dashID
            )
            this['B'] = arrayOf(
                dashID, inspID,
                dotID, inspID,
                dotID, inspID,
                dotID
            )
            this['C'] = arrayOf(
                dashID, inspID,
                dotID, inspID,
                dashID, inspID,
                dotID
            )

            this['D'] = arrayOf(
                dashID, inspID,
                dotID, inspID,
                dotID
            )
            this['E'] = arrayOf(
                dotID
            )
            this['F'] = arrayOf(
                dotID, inspID,
                dotID, inspID,
                dashID, inspID,
                dotID
            )
            this['G'] = arrayOf(
                dashID, inspID,
                dashID, inspID,
                dotID
            )
            this['H'] = arrayOf(
                dotID, inspID,
                dotID, inspID,
                dotID, inspID,
                dotID
            )
            this['I'] = arrayOf(
                dotID, inspID,
                dotID
            )
            this['J'] = arrayOf(
                dotID, inspID,
                dashID, inspID,
                dashID, inspID,
                dashID
            )
            this['K'] = arrayOf(
                dashID, inspID,
                dotID, inspID,
                dashID
            )
            this['L'] = arrayOf(
                dotID, inspID,
                dashID, inspID,
                dotID, inspID,
                dotID
            )
            this['M'] = arrayOf(
                dashID, inspID,
                dashID
            )
            this['N'] = arrayOf(
                dashID, inspID,
                dotID
            )
            this['O'] = arrayOf(
                dashID, inspID,
                dashID, inspID,
                dashID
            )
            this['P'] = arrayOf(
                dotID, inspID,
                dashID, inspID,
                dashID, inspID,
                dotID
            )
            this['Q'] = arrayOf(
                dashID, inspID,
                dashID, inspID,
                dotID, inspID,
                dashID
            )
            this['R'] = arrayOf(
                dotID, inspID,
                dashID, inspID,
                dotID
            )
            this['S'] = arrayOf(
                dotID, inspID,
                dotID, inspID,
                dotID
            )
            this['T'] = arrayOf(
                dashID
            )
            this['U'] = arrayOf(
                dotID, inspID,
                dotID, inspID,
                dashID
            )
            this['V'] = arrayOf(
                dotID, inspID,
                dotID, inspID,
                dotID, inspID,
                dashID
            )
            this['W'] = arrayOf(
                dotID, inspID,
                dashID, inspID,
                dashID
            )
            this['X'] = arrayOf(
                dashID, inspID,
                dotID, inspID,
                dotID, inspID,
                dashID
            )
            this['Y'] = arrayOf(
                dashID, inspID,
                dotID, inspID,
                dashID, inspID,
                dashID
            )
            this['Z'] = arrayOf(
                dashID, inspID,
                dashID, inspID,
                dotID, inspID,
                dotID
            )
            this['1'] = arrayOf(
                dotID, inspID,
                dashID, inspID,
                dashID, inspID,
                dashID, inspID,
                dashID
            )
            this['2'] = arrayOf(
                dotID, inspID,
                dotID, inspID,
                dashID, inspID,
                dashID, inspID,
                dashID
            )
            this['3'] = arrayOf(
                dotID, inspID,
                dotID, inspID,
                dotID, inspID,
                dashID, inspID,
                dashID
            )
            this['4'] = arrayOf(
                dotID, inspID,
                dotID, inspID,
                dotID, inspID,
                dotID, inspID,
                dashID
            )
            this['5'] = arrayOf(
                dotID, inspID,
                dotID, inspID,
                dotID, inspID,
                dotID, inspID,
                dotID
            )
            this['6'] = arrayOf(
                dashID, inspID,
                dotID, inspID,
                dotID, inspID,
                dotID, inspID,
                dotID
            )
            this['7'] = arrayOf(
                dashID, inspID,
                dashID, inspID,
                dotID, inspID,
                dotID, inspID,
                dotID
            )
            this['8'] = arrayOf(
                dashID, inspID,
                dashID, inspID,
                dashID, inspID,
                dotID, inspID,
                dotID
            )
            this['9'] = arrayOf(
                dashID, inspID,
                dashID, inspID,
                dashID, inspID,
                dashID, inspID,
                dotID
            )
            this['0'] = arrayOf(
                dashID, inspID,
                dashID, inspID,
                dashID, inspID,
                dashID, inspID,
                dashID
            )
            this['.'] = arrayOf(
                dotID, inspID,
                dashID, inspID,
                dotID, inspID,
                dashID, inspID,
                dotID, inspID,
                dashID
            )
            this[':'] = arrayOf(
                dashID, inspID,
                dashID, inspID,
                dashID, inspID,
                dotID, inspID,
                dotID, inspID,
                dotID,
            )
            this[','] = arrayOf(
                dashID, inspID,
                dashID, inspID,
                dotID, inspID,
                dotID, inspID,
                dashID, inspID,
                dashID
            )
            this['?'] = arrayOf(
                dotID, inspID,
                dotID, inspID,
                dashID, inspID,
                dashID, inspID,
                dotID, inspID,
                dotID
            )
            this['='] = arrayOf(
                dashID, inspID,
                dotID, inspID,
                dotID, inspID,
                dotID, inspID,
                dashID
            )
            this['\''] = arrayOf(
                dotID, inspID,
                dashID, inspID,
                dashID, inspID,
                dashID, inspID,
                dashID, inspID,
                dotID
            )
            this[';'] = arrayOf(
                dashID, inspID,
                dotID, inspID,
                dashID, inspID,
                dotID, inspID,
                dashID, inspID,
                dotID
            )
            this['/'] = arrayOf(
                dashID, inspID,
                dotID, inspID,
                dotID, inspID,
                dashID, inspID,
                dotID
            )
            this['!'] = arrayOf(
                dashID, inspID,
                dotID, inspID,
                dashID, inspID,
                dotID, inspID,
                dashID, inspID,
                dashID
            )
            this['-'] = arrayOf(
                dashID, inspID,
                dotID, inspID,
                dotID, inspID,
                dotID, inspID,
                dotID, inspID,
                dashID
            )
            this['_'] = arrayOf(
                dotID, inspID,
                dotID, inspID,
                dashID, inspID,
                dashID, inspID,
                dotID, inspID,
                dashID
            )
            this['\"'] = arrayOf(
                dotID, inspID,
                dashID, inspID,
                dotID, inspID,
                dotID, inspID,
                dashID, inspID,
                dotID
            )
            this['('] = arrayOf(
                dashID, inspID,
                dotID, inspID,
                dashID, inspID,
                dashID, inspID,
                dotID
            )
            this[')'] = arrayOf(
                dashID, inspID,
                dotID, inspID,
                dashID, inspID,
                dashID, inspID,
                dotID, inspID,
                dashID
            )
            this['$'] = arrayOf(
                dotID, inspID,
                dotID, inspID,
                dotID, inspID,
                dashID, inspID,
                dotID, inspID,
                dotID, inspID,
                dashID
            )
            this['&'] = arrayOf(
                dotID, inspID,
                dotID, inspID,
                dotID, inspID,
                dotID
            )
            this['@'] = arrayOf(
                dotID, inspID,
                dashID, inspID,
                dashID, inspID,
                dotID, inspID,
                dashID, inspID,
                dotID
            )
        }
    }

    private fun textToMorse(msg: String): List<Int> {
        val msgList = ArrayList<Int>()
        val charsMsg = prepareMessage(msg)
        for (c in charsMsg) {
            if (morseMap.containsKey(c)) {
                msgList.addAll(morseMap[c]!!)
                msgList.add(lspID)
            } else {
                when (c) {
                    ' ' -> msgList.add(wspID)
                }
            }
        }
        return msgList
    }

    private fun prepareMessage(msg: String): CharArray {
        val words = msg.uppercase(Locale.getDefault()).split("\\s".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val stringBuilder = StringBuilder()
        for (word in words) {
            if (word.isNotEmpty()) {
                stringBuilder.append(word)
                stringBuilder.append(" ")
            }
        }
        return stringBuilder.toString().toCharArray()
    }

    fun textToMorseMsg(msg: String): String {
        val outMsg = StringBuffer()
        val intMsg = textToMorse(msg)
        for (item in intMsg) {
            when (item) {
                dotID -> outMsg.append(DOT)
                dashID -> outMsg.append(DASH)
                inspID -> outMsg.append(INSP)
                lspID -> outMsg.append(LSP)
                wspID -> outMsg.append(WSP)
            }
        }
        return outMsg.toString()
    }

    fun textToMorseAudio(msg: String, filePath: String) {
        val intMsg = textToMorse(msg)
        val morseMidi = MorseCodePlayer()
        for (item in intMsg) {
            when (item) {
                dotID -> morseMidi.setDot()
                dashID -> morseMidi.setDash()
                inspID -> morseMidi.setInsp()
                lspID -> morseMidi.setLsp()
                wspID -> morseMidi.setWsp()
            }
        }
        try {
            morseMidi.writeToFile(filePath)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}