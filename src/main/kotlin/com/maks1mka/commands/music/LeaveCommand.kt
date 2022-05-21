package com.maks1mka.commands.music

import com.maks1mka.commands.BaseCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class LeaveCommand: BaseCommand() {
    override val name: String = "Leave"
    override val aliases: List<String> = listOf("leave", "disconnect", "stop")
    override val desc: String = "Отключиться от голосового канала"

    override fun onExecute(e: MessageReceivedEvent, name: String, args: List<String>) {

        if(isUserNotInVoice(e) || isBotNotInVoice(e)) return
        e.guild.audioManager.closeAudioConnection().apply {

            e.channel.sendMessage("Я отключилась от **${e.member!!.voiceState!!.channel!!.name}**").queue()
        }
        return

    }
}