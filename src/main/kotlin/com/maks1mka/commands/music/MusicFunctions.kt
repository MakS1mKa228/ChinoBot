package com.maks1mka.commands.music

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

fun isUserNotInVoice(event: MessageReceivedEvent): Boolean {
    return if (event.member?.voiceState?.inAudioChannel()!!) false
    else {
        event.channel.sendMessage("Ты не подключен к голосому каналу!").queue()
        true
    }
}

fun isBotNotInVoice(event: MessageReceivedEvent, message: Boolean = true): Boolean {
    return if (event.guild.selfMember.voiceState?.inAudioChannel()!!) false
    else {
        if (message) event.channel.sendMessage("Я не подключена к голосому каналу!").queue()
        true
    }
}