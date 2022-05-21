package com.maks1mka.commands.music

import com.maks1mka.commands.BaseCommand
import com.maks1mka.commands.music.lavaplayer.GuildMusicManager
import com.maks1mka.getTimestamp
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.managers.AudioManager
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import java.util.function.Consumer
import java.util.logging.Level
import java.util.logging.Logger

class PlayCommand: BaseCommand() {



    companion object Data {
        @JvmStatic lateinit var playerManager: AudioPlayerManager
        @JvmStatic lateinit var currentChannel: MessageChannel
        @JvmStatic lateinit var currentAuthor: Member
        @JvmStatic lateinit var currentGuild: Guild
        @JvmStatic lateinit var musicManagers: HashMap<String, GuildMusicManager>

        @JvmStatic
        fun getMusicManager(guild: Guild): GuildMusicManager {
            if (musicManagers[guild.id] == null) {
                synchronized(musicManagers) {
                    if (musicManagers[guild.id] == null) {
                        musicManagers[guild.id] = GuildMusicManager(playerManager)

                    }
                }
            }
            return musicManagers[guild.id]!!
        }
    }




    init {
        Logger.getLogger("org.apache.http.client.protocol.ResponseProcessCookies").level = Level.OFF

        playerManager = DefaultAudioPlayerManager()
        playerManager.registerSourceManager(YoutubeAudioSourceManager(true))
        playerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault())
        playerManager.registerSourceManager(BandcampAudioSourceManager())
        playerManager.registerSourceManager(VimeoAudioSourceManager())
        playerManager.registerSourceManager(TwitchStreamAudioSourceManager())
        playerManager.registerSourceManager(HttpAudioSourceManager())
        playerManager.registerSourceManager(LocalAudioSourceManager())

        musicManagers = HashMap<String, GuildMusicManager>(0)

    }

    override val name: String = "Play"
    override val aliases: List<String> = listOf("play", "p")
    override val desc: String = "Проиграть песню"


    override fun onExecute(e: MessageReceivedEvent, name: String, args: List<String>) {

        if(isBotNotInVoice(e, false)) JoinCommand().onExecute(e, name, args)

        if (args.isEmpty()) {
            e.channel.sendMessage("Ты не указал какую песню играть!").queue()
            return
        }

        val audio = e.guild.audioManager
        val manager: GuildMusicManager = getMusicManager(e.guild)
        currentChannel = e.channel
        currentAuthor = e.member!!
        currentGuild = e.guild

        val arg: String = args.joinToString(" ")
        println(arg)
        audio.sendingHandler = manager.sendHandler
        loadAndPlay(manager, e.channel, arg, true)
        return
    }
    private fun loadAndPlay(manager: GuildMusicManager, channel: MessageChannel, urlParams: String, isSearchCommand: Boolean) {
        var url: String = urlParams
        val trackUrl: String
        if (!isSearchCommand) {
            if (url.startsWith("<") && url.endsWith(">")) url = url.substring(1, url.length - 1)
            trackUrl = if (!url.startsWith("https://") && !url.startsWith("http://")) "ytsearch:$url" else url
        } else {
            trackUrl = "ytsearch:$url"
        }

        playerManager.loadItemOrdered(manager, trackUrl, object : AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack) {
                manager.scheduler.queue(track, false)
            }

            override fun playlistLoaded(playlist: AudioPlaylist) {
                if (!playlist.isSearchResult) {
                    val selectedTrackTrack = playlist.selectedTrack
                    val tracks = playlist.tracks
                    val durationPlaylist = AtomicLong()
                    if (selectedTrackTrack != null) {
                        manager.scheduler.queue(selectedTrackTrack, false)
                        return
                    }
                    if (tracks.size <= 100) {
                        tracks.forEach(Consumer { track: AudioTrack ->
                            durationPlaylist.addAndGet(
                                track.duration
                            )
                        })
                        val playlistAddEmbed: MessageEmbed = EmbedBuilder()
                            .setTitle("В очередь добавлено")
                            .addField("Количество треков", "" + tracks.size, false)
                            .addField(
                                "Длительность плейлиста",
                                "" + getTimestamp(durationPlaylist.get()),
                                false
                            )
                            .setColor(currentAuthor.user.retrieveProfile().complete()?.accentColor)
                            .setFooter(
                                "Запросил " + if (currentAuthor.nickname == null) currentAuthor.user.name else currentAuthor.nickname,
                                currentAuthor.user.avatarUrl
                            )
                            .build()
                        channel.sendMessageEmbeds(playlistAddEmbed).queue()
                        tracks.forEach(Consumer { track: AudioTrack? ->
                            manager.scheduler.queue(
                                track!!, true
                            )
                        })
                    } else {
                        channel.sendMessage("Невозможно добавить в очередь! В этом плейлисте более 100 треков!").queue()
                    }
                } else {
                    if (!isSearchCommand) {
                        val track = playlist.tracks[0]
                        manager.scheduler.queue(track, false)
                    } else {
                        val counter = AtomicLong()
                        val searchEmbed = EmbedBuilder()
                            .setTitle("Результаты поиска")
                        counter.addAndGet(1)
                        val searchResult: List<AudioTrack> = playlist.tracks.subList(0, 5)
                        val buttonMap: MutableMap<Long, Button>
                        buttonMap = HashMap()
                        searchResult.forEach(Consumer { track: AudioTrack ->
                            searchEmbed.addField(
                                counter.toString() + ")" + track.info.title + " [" + getTimestamp(
                                    track.duration
                                ) + "]", track.info.uri, false
                            )
                            buttonMap[counter.get()] = Button.primary(track.info.uri, counter.get().toString() + "")
                            counter.addAndGet(1)
                        })
                        channel.sendMessageEmbeds(searchEmbed.build()).setActionRow(buttonMap.values).queue()
                    }
                }
            }

            override fun noMatches() {
                channel.sendMessage("Ничего не найдено по $trackUrl").queue()
            }

            override fun loadFailed(exception: FriendlyException) {
                channel.sendMessage("Невозможно проиграть: " + exception.message).queue()
            }
        })
    }


    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        val audio: AudioManager? = event.guild?.audioManager
        val manager: GuildMusicManager = getMusicManager(event.guild!!)
        audio?.sendingHandler = manager.sendHandler
        currentChannel = event.channel
        currentAuthor = event.member!!
        println(event.componentId)
        loadAndPlay(manager, currentChannel, event.componentId, false)
        event.message.delete().queue()
        return
    }



}