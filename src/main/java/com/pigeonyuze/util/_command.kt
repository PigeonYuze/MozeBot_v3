package com.pigeonyuze.util

import net.mamoe.mirai.console.command.CommandContext
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SystemCommandSender
import net.mamoe.mirai.contact.AudioSupported
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.MessageReceipt
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.utils.ExternalResource
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

suspend fun CommandContext.quote(message: Message) =
    sender.subject?.sendMessage(message + originalMessage.quote()) ?: sender.sendMessage(message)

suspend fun CommandContext.quote(message: String) =
    sender.subject?.sendMessage(PlainText(message) + originalMessage.quote()) ?: sender.sendMessage(message)


suspend fun CommandContext.sendMessage(message: Message) =
    this.sender.subject?.sendMessage(message) ?: this.sender.sendMessage(message)

suspend fun CommandContext.uploadAudio(resource: ExternalResource): OfflineAudio =
    (sender.subject as? AudioSupported)?.uploadAudio(resource)
        ?: throw IllegalArgumentException("Cannot send audio to unsupported context!")

suspend fun CommandContext.uploadImage(resource: ExternalResource): Image =
    sender.subject!!.uploadImage(resource)

/**
 * Mapping to [buildMessageChain]
 * */
@OptIn(ExperimentalContracts::class)
suspend fun CommandContext.sendMessageChain(block: suspend MessageChainBuilder.() -> Unit): MessageReceipt<Contact>? {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    val builder = MessageChainBuilder()
    block.invoke(builder)
    return if (sender.subject != null) quote(builder.build()) else sendMessage(builder.build())
}

/**
 * 构造一条 [ForwardMessage]
 *
 * @see ForwardMessageBuilder 查看 DSL 帮助
 * @see ForwardMessage 查看转发消息说明
 */
@JvmSynthetic
suspend inline fun CommandContext.sendForwardMessage(
    displayStrategy: ForwardMessage.DisplayStrategy = ForwardMessage.DisplayStrategy,
    block: ForwardMessageBuilder.() -> Unit
) = subject.sendMessage(buildForwardMessage(subject, displayStrategy, block))

suspend fun MessageChain.quoteTo(origin: MessageChain,withContact: Contact) = withContact.sendMessage(origin.quote() + this)

val CommandContext.fromGroup get() = (sender.subject as Group).id
val CommandContext.fromContact get() = (sender.subject as Contact).id
val CommandContext.fromQQ get() = sender.user?.id ?: -1
val CommandContext.from get() = sender.user
val CommandContext.bot get() = sender.bot
val CommandContext.subject: Contact get() = sender.subject!!

val CommandSender.isByConsole: Boolean get() = this is SystemCommandSender