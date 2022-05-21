package com.maks1mka.commands.music

import com.maks1mka.commands.BaseCommand
import net.dv8tion.jda.api.entities.AudioChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class JoinCommand: BaseCommand() {
    override val name: String = "Join"
    override val aliases: List<String> = listOf("join", "connect", "start")
    override val desc: String = "Подключиться к голосовому каналу"

    override fun onExecute(e: MessageReceivedEvent, name: String, args: List<String>) {

        if(isUserNotInVoice(e)) return

        val channel: AudioChannel = e.member!!.voiceState!!.channel!!
        e.guild.audioManager.openAudioConnection(channel).apply {
            e.guild.audioManager.sendingHandler = null
            e.channel.sendMessage("Я подключилась к **${channel.name}**").queue()
        }

        return

    }
}