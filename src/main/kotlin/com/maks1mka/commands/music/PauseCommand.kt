package com.maks1mka.commands.music

import com.maks1mka.commands.BaseCommand
import com.maks1mka.commands.music.lavaplayer.GuildMusicManager
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class PauseCommand: BaseCommand() {
    override val name: String = "Pause"
    override val aliases: List<String> = listOf("pause", "resume")
    override val desc: String = "Приостановить или возобновить проигрывание музыки"

    override fun onExecute(e: MessageReceivedEvent, name: String, args: List<String>) {

        if(isUserNotInVoice(e) || isBotNotInVoice(e)) return

        val manager: GuildMusicManager = PlayCommand.getMusicManager(e.guild)
        if (manager.player.isPaused) {
            manager.player.isPaused = false
            e.channel.sendMessage("Проигрывание трека возобновлено!")
        }
        else {
            manager.player.isPaused = true
            e.channel.sendMessage("Проигрывание трека приостановлено!")
        }
        return
    }

}