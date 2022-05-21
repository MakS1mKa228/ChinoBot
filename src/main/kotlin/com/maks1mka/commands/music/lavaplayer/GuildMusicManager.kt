package com.maks1mka.commands.music.lavaplayer

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager

class GuildMusicManager(manager: AudioPlayerManager) {

    val player: AudioPlayer

    val scheduler: TrackScheduler

    val sendHandler: AudioPlayerSendHandler

    init {
        this.player = manager.createPlayer()
        this.scheduler = TrackScheduler(this.player)
        sendHandler = AudioPlayerSendHandler(this.player)
        player.addListener(scheduler)

    }
}