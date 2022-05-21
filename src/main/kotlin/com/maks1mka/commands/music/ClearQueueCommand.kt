package com.maks1mka.commands.music

import com.maks1mka.commands.BaseCommand
import com.maks1mka.commands.music.lavaplayer.GuildMusicManager
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class ClearQueueCommand: BaseCommand() {
    override val name: String = "Clear"
    override val aliases: List<String> = listOf("clear", "clearqueue", "clearlist")
    override val desc: String = "Очистить музыкальную очередь"

    override fun onExecute(e: MessageReceivedEvent, name: String, args: List<String>) {

        if(isUserNotInVoice(e) || isBotNotInVoice(e)) return

        val manager: GuildMusicManager = PlayCommand.getMusicManager(e.guild)
        manager.player.destroy().apply {
            e.channel.sendMessage("Очередь успешно очищена!").queue()
        }
        return
    }


}