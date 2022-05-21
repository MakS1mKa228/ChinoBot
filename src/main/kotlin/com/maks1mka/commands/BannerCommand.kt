package com.maks1mka.commands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class BannerCommand: BaseCommand() {
    override val name: String = "Banner"
    override val aliases: List<String> = listOf("banner", "bann")
    override val desc: String = "Display the banner of the specified user"

    override fun onExecute(e: MessageReceivedEvent, name: String, args: List<String>) {

        val user: User = if (e.message.mentionedUsers.size == 0) if (args.isNotEmpty()) e.jda.retrieveUserById(args[0])
            .complete() else e.message.author else e.message.mentionedUsers[0]

        val embed: MessageEmbed;
        if(user.retrieveProfile().complete().bannerId.isNullOrEmpty()) {
            embed = EmbedBuilder()
                .setTitle("Error!")
                .setDescription("${user.asMention} does not have a banner")
                .setImage("https://aniyuki.com/wp-content/uploads/2021/09/aniyuki-sad-anime-gif-63.gif")
                .setFooter("Requested ${e.author.name}")
                .setColor(user.retrieveProfile().complete().accentColor)
                .build()
        } else {
            embed = EmbedBuilder()
                .setTitle("Баннер ${user.asTag}")
                .setImage("${user.retrieveProfile().complete().bannerUrl}?size=4096")
                .setFooter("Запросил ${e.author.name}")
                .setColor(user.retrieveProfile().complete().accentColor)
                .build()
        }
        e.channel.sendMessageEmbeds(embed).queue()
    }

}