package com.maks1mka.commands

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class PingCommand: BaseCommand() {
    override val name: String = "Ping"
    override val aliases: List<String> = listOf("ping", "pg", "pong")
    override val desc: String = "Check connection quality"

    override fun onExecute(e: MessageReceivedEvent, name: String, args: List<String>) {
        e.channel.sendMessage("Ping: ${e.jda.gatewayPing}ms").queue()
        return
    }

}