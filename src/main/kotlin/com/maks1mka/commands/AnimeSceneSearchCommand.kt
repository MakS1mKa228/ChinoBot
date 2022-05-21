package com.maks1mka.commands

import com.github.tsohr.JSONArray
import com.github.tsohr.JSONObject
import com.maks1mka.getTimestamp
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.awt.Color
import java.net.URL
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit


class AnimeSceneSearchCommand: BaseCommand() {

    override val name: String = "AnimeSceneSearch"
    override val aliases: List<String> = listOf("anime", "animesearch")
    override val desc: String = "Найти аниме по картинке"

    private var baseUrl: String = "https://api.trace.moe/search?anilistInfo&"

    override fun onExecute(e: MessageReceivedEvent, name: String, args: List<String>) {
        if(args.isEmpty() && e.message.attachments.isEmpty() && !e.message.attachments[0].isImage) return
        var imageUrl: String = ""

        if (e.message.attachments.isNotEmpty() && args.isEmpty()) {
            imageUrl = e.message.attachments[0].url
        } else if(args.isNotEmpty()) {
            imageUrl = args[0]
        }


        var result = JSONObject(URL("$baseUrl&url=$imageUrl").readText(Charsets.UTF_8))

        if (result.getString("error") != "") {
            e.channel.sendMessage("Произошла ошибка: ${result.getString("error")}").queue()
            return
        }

        println(result.toString())
        result = result.getJSONArray("result").getJSONObject(0)
        println(result.toString())

        val titleName: String = result.getJSONObject("anilist").getJSONObject("title").getString("romaji")

        val episodeNumber: Int = result.getInt("episode")

        val timeStamp: String = getTimestamp(result.getLong("from").seconds.inWholeMilliseconds)

        val embed: MessageEmbed = EmbedBuilder()
            .setTitle("Информация")
            .setColor(e.author.retrieveProfile().complete().accentColor)
            .setDescription("**Название:** $titleName\n**Серия:** $episodeNumber\n**Метка времени:** $timeStamp")
            .setFooter("Запросил ${e.author.name}", e.author.effectiveAvatarUrl)
            .build()

        e.channel.sendMessageEmbeds(embed).queue()
        return

    }

}