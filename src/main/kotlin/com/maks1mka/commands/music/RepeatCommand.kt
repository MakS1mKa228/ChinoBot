package com.maks1mka.commands.music

import com.maks1mka.commands.BaseCommand
import com.maks1mka.commands.music.PlayCommand.Data.getMusicManager
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class RepeatCommand: BaseCommand() {
    override val name: String = "Repeat"
    override val aliases: List<String> = listOf("repeat", "loop")
    override val desc: String = "Повторить трек"

    override fun onExecute(e: MessageReceivedEvent, name: String, args: List<String>) {
        val manager = getMusicManager(e.guild)
        manager.scheduler.setRepeating(!manager.scheduler.isRepeating())
        e.channel.sendMessage("Повторение трека **" + (if (manager.scheduler.isRepeating()) "включено" else "выключено") + "**").queue()
    }

}