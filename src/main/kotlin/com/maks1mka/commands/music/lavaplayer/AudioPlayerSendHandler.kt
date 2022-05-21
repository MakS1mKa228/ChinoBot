package com.maks1mka.commands.music.lavaplayer

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame
import net.dv8tion.jda.api.audio.AudioSendHandler
import java.nio.ByteBuffer


class AudioPlayerSendHandler(audioPlayer: AudioPlayer): AudioSendHandler {

    private val audioPlayer: AudioPlayer
    private var lastFrame: AudioFrame? = null

    init {
        this.audioPlayer = audioPlayer
    }
    override fun canProvide(): Boolean {
        if (lastFrame == null) {
            lastFrame = audioPlayer.provide()
        }

        return lastFrame != null
    }

    override fun provide20MsAudio(): ByteBuffer? {
        if (lastFrame == null) {
            lastFrame = audioPlayer.provide()
        }

        val data: ByteArray? = lastFrame?.data
        lastFrame = null

        return ByteBuffer.wrap(data)
    }

    override fun isOpus(): Boolean {
        return true
    }
}