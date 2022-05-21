package com.maks1mka.commands.music.lavaplayer

import com.maks1mka.commands.music.PlayCommand
import com.maks1mka.commands.music.PlayCommand.Data.getMusicManager
import com.maks1mka.commands.music.PlayCommand.Data.musicManagers
import com.maks1mka.getTimestamp
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import java.util.*
import java.util.Collections.shuffle

class TrackScheduler(private var player: AudioPlayer): AudioEventAdapter() {

    private var repeating: Boolean = false
    private var queue: Queue<AudioTrack> = LinkedList()
    private var playingMessageid: Long = 0
    private lateinit var lastTrack: AudioTrack


    fun queue(track: AudioTrack, isPlaylist: Boolean) {
        if (!player.startTrack(track, true)) {
            if (!isPlaylist) {
                val currentAuthor: Member = PlayCommand.currentAuthor
                val position = queue.size + 1
                var duration: String? = "__"
                if (!track.info.isStream) duration = getTimestamp(track.duration)
                val playingEmbed = EmbedBuilder()
                    .setTitle("В очередь добавлено")
                    .setDescription("**" + track.info.title + "**")
                    .addField("Позиция в очереди", "" + position, false)
                    .addField("Длительность", duration, true)
                    .addField("Ссылка", track.info.uri, true)
                    .setColor(PlayCommand.currentAuthor.user.retrieveProfile().complete().accentColor)
                    .setFooter(
                        "Запросил " + if (currentAuthor.nickname == null) currentAuthor.user.name else currentAuthor.nickname,
                        currentAuthor.user.avatarUrl
                    )
                    .build()
                PlayCommand.currentChannel.sendMessageEmbeds(playingEmbed).queue()
            }
            queue.add(track)
        }
    }

    fun nextTrack() { player.startTrack(queue.poll(), false) }

    override fun onTrackStart(player: AudioPlayer?, track: AudioTrack) {
        val currentAuthor: Member = PlayCommand.currentAuthor
        var duration: String? = "__"
        if (!track.info.isStream) duration = getTimestamp(track.duration)
        val playingEmbed = EmbedBuilder()
            .setTitle("Сейчас проигрывается")
            .setDescription("**" + track.info.title + "**")
            .addField("Длительность", duration, true)
            .addField("Ссылка", track.info.uri, true)
            .setColor(currentAuthor.user.retrieveProfile().complete().accentColor)
            .setFooter(
                "Запросил " + if (currentAuthor.nickname.isNullOrEmpty()) currentAuthor.user.name else currentAuthor.nickname,
                currentAuthor.user.avatarUrl
            )
            .build()
        PlayCommand.currentChannel.sendMessageEmbeds(playingEmbed)
            .queue { result -> playingMessageid = result.idLong }
    }

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        lastTrack = track
        if (queue.isEmpty()) {
            if (playingMessageid != 0L) PlayCommand.currentChannel.deleteMessageById(playingMessageid).queue()
            PlayCommand.currentChannel.sendMessage("Очередь закончилась, выхожу из канала!").queue()
            val manager: GuildMusicManager = getMusicManager(PlayCommand.currentGuild)
            musicManagers.values.remove(manager)
            PlayCommand.currentGuild.audioManager.closeAudioConnection()
        } else if (endReason.mayStartNext) {
            if (repeating) {
                if (playingMessageid != 0L) PlayCommand.currentChannel.deleteMessageById(playingMessageid).queue()
                player.startTrack(lastTrack.makeClone(), false)
            } else {
                if (playingMessageid != 0L) PlayCommand.currentChannel.deleteMessageById(playingMessageid).queue()
                nextTrack()
            }
        }
    }


    fun isRepeating(): Boolean { return repeating }

    fun setRepeating(repeating: Boolean) { this.repeating = repeating }

    fun shuffle() { shuffle(queue as List<*>) }
}