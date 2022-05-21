package com.maks1mka.commands

import com.github.tsohr.JSONObject
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.net.URL

class QuoteCommand: BaseCommand() {
    override val name: String = "Quote"
    override val aliases: List<String> = listOf("quote", "quoteanime")
    override val desc: String = "Случайная цитата из какого-то аниме"

    private val baseURL: String = "https://animechan.vercel.app/api/random"

    override fun onExecute(e: MessageReceivedEvent, name: String, args: List<String>) {
        val result = JSONObject(URL(baseURL).readText(Charsets.UTF_8))

        val embed: MessageEmbed = EmbedBuilder()
            .setTitle("Цитата из аниме")
            .setDescription("**Название аниме:** ${result.getString("anime")}\n**Персонаж:** ${result.getString("character")}\n**Цитата:** ${result.getString("quote")}")
            .build()

        e.channel.sendMessageEmbeds(embed).queue()
        return
    }
}