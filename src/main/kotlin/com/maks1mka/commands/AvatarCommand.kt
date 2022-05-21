package com.maks1mka.commands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class AvatarCommand: BaseCommand() {
    override val name: String = "Avatar"
    override val aliases: List<String> = listOf("avatar", "ava")
    override val desc: String = "Display the avatar of the specified user"

    override fun onExecute(e: MessageReceivedEvent, name: String, args: List<String>) {

        val user: User = if (e.message.mentionedUsers.size == 0) if (args.isNotEmpty()) e.jda.retrieveUserById(args[0])
            .complete() else e.message.author else e.message.mentionedUsers[0]

        val embed: MessageEmbed = EmbedBuilder()
            .setTitle("Аватарка ${user.asTag}")
            .setImage("${user.effectiveAvatarUrl}?size=4096")
            .setFooter("Запросил ${e.author.name}")
            .setColor(user.retrieveProfile().complete().accentColor)
            .build()

        e.channel.sendMessageEmbeds(embed).queue()
    }

}