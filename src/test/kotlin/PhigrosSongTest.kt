import com.pigeonyuze.command.phigros.data.PhigrosSongManager

object PhigrosSongTest {
    @JvmStatic
    fun main(args: Array<String>) {
        PhigrosSongManager.init()
        while (true) {
            val ln = readln()
            val phigrosSongs = PhigrosSongManager.PHIGROS_SONG_LIST.filter { it.matchingEqual(ln) }
            phigrosSongs.forEach {
                println(it.humanReadable())
                println()
            }
            println()
        }
    }
}