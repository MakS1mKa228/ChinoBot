package com.maks1mka.commands

import com.maks1mka.getPrefix
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

abstract class BaseCommand: ListenerAdapter() {

    abstract val name: String
    abstract val aliases: List<String>
    abstract val desc: String
    abstract fun onExecute(e: MessageReceivedEvent, name: String, args: List<String>)

    override fun onMessageReceived(event: MessageReceivedEvent) {
        val content: String = event.message.contentRaw
        if(event.author.isBot || !content.startsWith(getPrefix())) return
        var args: List<String> = content.substringAfter(getPrefix()).split(" ")
        val name = args[0]
        args = args.subList(1, args.size)
        if(name == this.name || this.aliases.contains(name)) {
            onExecute(event, name, args)
            println("[INFO] ${event.author.name} - ${event.author.id} - ${this.name}")
        }
    }


}