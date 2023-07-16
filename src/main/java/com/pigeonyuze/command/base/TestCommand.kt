package com.pigeonyuze.command.base

import com.pigeonyuze.MozeBotCore
import com.pigeonyuze.account.MemoryUser
import com.pigeonyuze.account.UserManager
import com.pigeonyuze.account.UserManager.incrementCoin
import com.pigeonyuze.util.isByConsole
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.contact.PermissionDeniedException

object TestCommand : CompositeCommand(
    owner = MozeBotCore,"test", description = "测试指令"
){
    // Fuck mirai-console!
    // You can't use these names to try to name your sub command at all
    // > '\/!@#$%^&*()_+-={}[];':",.<>?`~'
    @SubCommand("add|user||reg")
    @Description("Add a new MemoryUser to data")
    suspend fun CommandSender.addUserReg(
        uid: Int,
        canFreeSetName: Boolean,
        coin: Int,
        exp: Long,
        level: Int,
        qqId: Long,
        regDate: String,
        userName: String
    ) {
        isByConsole || throw PermissionDeniedException()
        MemoryUser(
            uid, canFreeSetName, coin, exp, level, qqId, regDate, userName
        ).also { it.reg() }
        sendMessage("Done.")
        sendMessage("Now: ${MemoryUser.data}")
    }

    @SubCommand("add|user||reg")
    @Description("Add a new MemoryUser to data by system")
    suspend fun CommandSender.addUserBySystem(traceId: Long = -1L){
        isByConsole || throw PermissionDeniedException()
        UserManager.regUserOf(traceId)
        sendMessage("Done.")
        sendMessage("Now ${MemoryUser.data}")
    }

    @SubCommand("set|UserData||save")
    @Description("Call save function")
    suspend fun CommandSender.saveRightNow(){
        isByConsole || throw PermissionDeniedException()
        sendMessage("Try to call function: write();")
        UserManager.write(UserManager.WriteOption.CLOSE_PLUGIN)
        sendMessage("Done.")
    }

    @SubCommand("set|UserData||add")
    suspend fun CommandSender.addCoin(uid: Int,plus: Int) {
        isByConsole || throw PermissionDeniedException()
        UserManager[uid].incrementCoin(plus)
        sendMessage("Done. \nNow_User_Object=${UserManager[uid]}")
    }
}