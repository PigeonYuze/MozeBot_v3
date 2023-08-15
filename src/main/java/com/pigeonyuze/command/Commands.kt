package com.pigeonyuze.command

import com.pigeonyuze.command.account.OthersAccountInfoCommand
import com.pigeonyuze.command.account.SelfAccountInfoCommand
import com.pigeonyuze.command.account.SignCommand
import com.pigeonyuze.command.account.giftcode.GiftCodeCommand
import com.pigeonyuze.command.account.redpacket.RedPacketCommand
import com.pigeonyuze.command.base.*
import com.pigeonyuze.command.bili.mirai.BiliCommand
import com.pigeonyuze.command.phigros.PhigrosCommand
import com.pigeonyuze.command.recreation.fb.FbCommand
import com.pigeonyuze.command.recreation.morse.MorseCommand
import com.pigeonyuze.command.recreation.random.BaseRandomCommand
import com.pigeonyuze.util.loggingDebug
import com.pigeonyuze.util.loggingInfo
import com.pigeonyuze.util.loggingVerbose
import net.mamoe.mirai.console.command.Command
import net.mamoe.mirai.console.command.CommandManager
import java.util.jar.JarFile
import kotlin.reflect.jvm.jvmName

object Commands {
    private val commands = listOf<Command>(
        PhigrosCommand,
        BaseRandomCommand,
        ThrowCoinCommand,
        MorseCommand,
        SignCommand,
        SelfAccountInfoCommand,
        OthersAccountInfoCommand,
        GiftCodeCommand,
        RedPacketCommand,
        FbCommand,
        ToolCommand,
        BiliCommand,
        SubmissionCommand,
        BotCommand,
        TestCommand
    )


    fun loadByClass() {
        val currentDirectory = this::class.jvmName /* Class name with package name(suck as 'com.pigeonyuze.command.Command') */
            .substringBeforeLast('.') /* Drops class name(such as 'Command') */
            .replace('.','/') /* Gets jar entry name */
        val codeSource = Commands.javaClass.protectionDomain.codeSource.location
        require(codeSource.file.endsWith(".jar")) { "Can not run this function from class file. Please call this function by jar file! " }
        val classLoader = Commands.javaClass.classLoader
        loggingDebug { "Starts load each of commands with classLoader($classLoader)" }
        val jar = JarFile(codeSource.file)
        loggingDebug { "Find jar file: $jar" }
        val jarEntries = jar.entries().asIterator()
        for (entry in jarEntries) {
            if (!entry.name.startsWith(currentDirectory) || entry.name.contains("$"))
                continue
            /* Parses as package name */
            val className = entry.name.replace('/','.').removeSuffix(".class")
            val classObject = classLoader.loadClass(className)
            if (!classObject.isAssignableFrom(Command::class.java))
                continue
            loggingVerbose { "Loads command file $classObject" }
            /* Always, any subclass of command class has field 'INSTANCE' (Kotlin 'object' will create field automatically) */
            /*  such as:
             *   @NotNull
             *   public static final Command INSTANCE;
            * */

            val instance = classObject.fields.find { it.name == "INSTANCE" }!!
            val command = instance.get(null) as Command
            CommandManager.registerCommand(command)
        }
        loggingInfo { "Registered commands." }
    }

    fun load() {
        for (command in commands) {
            CommandManager.registerCommand(command)
        }
    }
}