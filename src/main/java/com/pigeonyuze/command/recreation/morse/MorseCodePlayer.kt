package com.pigeonyuze.command.recreation.morse

import java.io.FileOutputStream
import java.io.IOException
import java.util.ArrayList

internal open class MorseCodePlayer {
    val DOT = 8
    val DASH = 24
    val INSP = 8
    val LSP = 24
    val WSP = 56
    val VELOCITY = 70

    val header = intArrayOf(
        0x4d, 0x54, 0x68, 0x64, 0x00, 0x00, 0x00, 0x06,
        0x00, 0x00,
        0x00, 0x01,
        0x00, 0x10,  // set 16 ticks per quarter note
        0x4d, 0x54, 0x72, 0x6B
    )

    val footer = intArrayOf(
        0x01, 0xFF, 0x2F, 0x00
    )

    val tempoEvent = intArrayOf(
        0x00, 0xFF, 0x51, 0x03,
        0x01, 0xD4, 0xC0 // Tempo (period) 150000
    )

    val keySigEvent = intArrayOf(
        0x00, 0xFF, 0x59, 0x02,
        0x00,
        0x00
    )

    val timeSigEvent = intArrayOf(
        0x00, 0xFF, 0x58, 0x04,
        0x04,
        0x02,
        0x30,
        0x08
    )

    fun intArrayToByteArray(ints: IntArray): ByteArray {
        val l = ints.size
        val out = ByteArray(ints.size)
        for (i in 0 until l) {
            out[i] = ints[i].toByte()
        }
        return out
    }

    var playEvents: ArrayList<IntArray> = ArrayList()

    init {
        progChange()
    }


    @Throws(IOException::class)
    fun writeToFile(filename: String) {
        val fos = FileOutputStream(filename)
        fos.write(intArrayToByteArray(header))
        var size = tempoEvent.size + keySigEvent.size + timeSigEvent.size + footer.size
        for (element in playEvents) size += element.size

        // Write out the track data size in big-endian format
        val high = size / 256
        val low = size - high * 256
        fos.write(0)
        fos.write(0)
        fos.write(high)
        fos.write(low)
        fos.write(intArrayToByteArray(tempoEvent))
        fos.write(intArrayToByteArray(keySigEvent))
        fos.write(intArrayToByteArray(timeSigEvent))

        // Write out the note and events
        for (element in playEvents) {
            fos.write(intArrayToByteArray(element))
        }

        // Write the footer and close
        fos.write(intArrayToByteArray(footer))
        fos.flush()
        fos.close()

    }

    fun setDot() {
        setNote(DOT, VELOCITY)
    }

    fun setDash() {
        setNote(DASH, VELOCITY)
    }

    fun setInsp() {
        setNote(INSP, 0)
    }

    fun setLsp() {
        setNote(LSP, 0)
    }

    fun setWsp() {
        setNote(WSP, 0)
    }

    //Change midi instruments from 0 to 127
    private fun progChange() {
        val data = IntArray(3)
        data[0] = 0
        data[1] = 0xC0
        data[2] = 0x10
        playEvents.add(data)
    }

    private fun setNote(duration: Int, velocity: Int) {
        noteStart(velocity)
        noteEnd(duration)
    }

    private fun noteStart(velocity: Int) {
        val data = IntArray(4)
        data[0] = 0x0
        data[1] = 0x90
        data[2] = 0x48
        data[3] = velocity
        playEvents.add(data)
    }

    private fun noteEnd(delta: Int) {
        val data = IntArray(4)
        data[0] = delta
        data[1] = 0x80
        data[2] = 0x48
        data[3] = 0
        playEvents.add(data)
    }
}