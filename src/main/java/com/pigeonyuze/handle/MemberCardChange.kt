package com.pigeonyuze.listener.handle

import net.mamoe.mirai.event.events.MemberCardChangeEvent

object MemberCardChange :EventHandle<MemberCardChangeEvent> {

    private val blockedList = listOf(
        "傻",
        "狗",
        "逼",
        "爬",
        "比",
        "几把",
        "jb",
        "超",
        "doi",
        "rbq"
    )

    override suspend fun handle(event: MemberCardChangeEvent) {
        if (event.member.id != event.bot.id && event.new != event.bot.nick) return
        val name = event.new.replace(" ".toRegex(), "").lowercase()
        for (word in blockedList) {
            if (name.contains(word)) {
                event.group.botAsMember.nameCard = event.bot.nick
                event.group.sendMessage("请不要将我的名字改成这个＞﹏＜.....谢谢...")
                return
            }
        }
        event.group.sendMessage("是新的名字！ $name ？\n听起来感觉还行。？( •̀ ω •́ )✧")

    }
}