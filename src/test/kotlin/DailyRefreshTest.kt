import com.pigeonyuze.util.DailyRefreshTasks
import com.pigeonyuze.util.debugging
import kotlinx.coroutines.delay
import me.him188.kotlin.jvm.blocking.bridge.JvmBlockingBridge
import java.time.LocalDateTime
import java.time.LocalTime

object DailyRefreshTest {
    class Test : DailyRefreshTasks {
        @DailyRefreshTasks.Refresh
        var test = 0

        init {
            this.initRefresh()
        }
    }

    @JvmStatic
    @JvmBlockingBridge
    suspend fun main(args: Array<String>) {
        println(LocalDateTime.now().with(LocalTime.MIDNIGHT))
        debugging = true
        val test = Test()
        test.test++
        println("Test++")
        while (true) {
            println(test.test)
            if (test.test != 1) break
            delay(1000)
        }

        println("end")
    }
}