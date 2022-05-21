package com.maks1mka.commands

import com.github.tsohr.JSONObject
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.net.URL
import java.util.*

class ActionCommand : BaseCommand() {
    private var dict: TreeMap<String, String> = TreeMap()
    //private var coreUrl: String = "https://discord-holo-api.ml:8080/api"
    private var coreUrl: String = "http://api.nekos.fun:8080/api"
    private var keyJSON: String = "image"
    init {
        with(this.dict) {
            put("anal", "вставил(-а) в попку")
            put("bite", "укусил(-а)")
            put("cry", "заплакал(-а)")
            put("cum", "кончил(-а) в(на)")
            put("dance", "потанцевал(-а) с")
            put("feed", "накормил(-а)")
            put("hug", "обнял(-а)")
            put("kiss", "поцеловал(-а)")
            put("pat", "погладил(-а)")
            put("poke", "тыкнул(-а)")
            put("punch", "ударил(-а)")
            put("slap", "дал(-а) пощёчину")
            put("tickle", "защекотал(-а)")
            put("wink", "подмигнул(-а)")
            put("baka", "назвал(-а) дурашкой")
        }
    }

    override val name: String = "Action"
    override val aliases: List<String> = this.dict.keys.toList()
    override val desc: String = "Nothing"


    override fun onExecute(e: MessageReceivedEvent, name: String, args: List<String>) {

        val tag: String = name
        val data = JSONObject(URL("$coreUrl/$tag").readText(Charsets.UTF_8))

        if (tag == "cry"){
            val embed: MessageEmbed = EmbedBuilder()
                .setFooter("Запросил ${e.author.name}")
                .setImage(data.getString(this.keyJSON))
                .setColor(e.author.retrieveProfile().complete().accentColor)
                .build()
            e.channel.sendMessageEmbeds(embed).queue()
            return
        }

        val member: Member? =
            if (e.message.mentionedMembers.isEmpty())
                if (args.isNotEmpty()) e.guild.getMemberById(args[0])
                else e.member
            else e.message.mentionedMembers[0]

        if(member == e.member) {
            e.channel.sendMessage("Нельзя употреблять это действие на себе, дурашка :3").queue()
            return
        }

        if (member?.javaClass?.simpleName != "MemberImpl") {
            e.channel.sendMessage("Произошла какая-то ошибка! Ты точно нигде не ошибся, дурашка?").queue()
            return
        }
        val embed: MessageEmbed = EmbedBuilder()
            .setFooter("Requested by ${e.author.name}")
            .setImage(data.getString(this.keyJSON))
            .setColor(member.user.retrieveProfile().complete().accentColor)
            .setDescription("${e.author.asMention} **${dict[tag]}** ${member.asMention}")
            .build()
        e.channel.sendMessageEmbeds(embed).queue()

    }
}