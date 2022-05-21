package com.maks1mka.commands.music

import com.maks1mka.commands.BaseCommand
import com.maks1mka.commands.music.lavaplayer.GuildMusicManager
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class SkipCommand: BaseCommand() {
    override val name: String = "Skip"
    override val aliases: List<String> = listOf("skip", "sk", "s")
    override val desc: String = "Пропустить песню"

    override fun onExecute(e: MessageReceivedEvent, name: String, args: List<String>) {

        if(isUserNotInVoice(e) || isBotNotInVoice(e)) return

        val manager: GuildMusicManager = PlayCommand.getMusicManager(e.guild)
        manager.scheduler.nextTrack()
        return
    }

}