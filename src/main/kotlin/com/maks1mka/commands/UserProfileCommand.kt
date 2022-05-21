package com.maks1mka.commands

import com.maks1mka.getGuildForEmoji
import com.maks1mka.getTimestamp
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Emote
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.util.*

class UserProfileCommand: BaseCommand() {
    override val name: String = "UserProfile"
    override val aliases: List<String> = listOf("userprofile", "user", "profile", "pr", "info")
    override val desc: String = "Display the profile of the specified user"

    override fun onExecute(e: MessageReceivedEvent, name: String, args: List<String>) {
        val member: Member? =
                if (e.message.mentionedMembers.isEmpty())
                    if (args.isNotEmpty()) e.guild.getMemberById(args[0])
                    else e.message.member
                else e.message.mentionedMembers[0]


        if (member?.javaClass?.simpleName != "MemberImpl") {
            e.channel.sendMessage("Произошла какая-то ошибка! Ты точно нигде не ошибся, дурашка?").queue()
            return
        }

        var onlineStatus: String
        var userStatus = ""
        onlineStatus = member.onlineStatus.name
        when (onlineStatus) {
            "DO_NOT_DISTURB" -> {
                val dnd: Emote =
                    Objects.requireNonNull(e.jda.getGuildById(getGuildForEmoji()))!!.getEmotesByName("dnd", false)[0]
                onlineStatus = dnd.asMention + "Не беспокоить"
            }
            "OFFLINE" -> {
                val off: Emote = Objects.requireNonNull(e.jda.getGuildById(getGuildForEmoji()))!!.getEmotesByName("off", false)[0]
                onlineStatus = off.asMention + "Не в сети"
            }
            "ONLINE" -> {
                val on: Emote = Objects.requireNonNull(e.jda.getGuildById(getGuildForEmoji()))!!.getEmotesByName("on", false)[0]
                onlineStatus = on.asMention + "В сети"
            }
            "IDLE" -> {
                val idle: Emote = Objects.requireNonNull(e.jda.getGuildById(getGuildForEmoji()))!!.getEmotesByName("idle", false)[0]
                onlineStatus = idle.asMention + "Не активен"
            }
        }
        var userActivityString = "_Без активности_"
        if (member.activities.isNotEmpty()) {
            for(userActivity in member.activities) {
                var typeActivity = userActivity.type.name
                println(userActivity.name)
                when (typeActivity) {
                    "WATCHING" -> typeActivity = "**Смотрит** "
                    "LISTENING" -> typeActivity = "**Слушает** "
                    "STREAMING" -> typeActivity = "**Стримит** "
                    "PLAYING" -> typeActivity = "**Играет в** "
                    "CUSTOM_STATUS" -> typeActivity =
                        (if (userActivity.emoji != null) userActivity.emoji!!.asMention else "") + " "
                }
                val nameActivity: String = typeActivity + userActivity.name
                var state: String? = userActivity.asRichPresence()?.state
                if(state.isNullOrEmpty()) state = "_Без статуса_"
                userActivityString = if (userActivity.timestamps != null) {
                    val startTime = userActivity.timestamps!!.start
                    val currentTime = Date().time
                    "$nameActivity \n**Состояние:** $state \n**Прошло:** ${getTimestamp(currentTime - startTime)}".trimIndent()
                } else {
                    if (userActivity.type.name == "CUSTOM_STATUS") {
                        userStatus = nameActivity
                        ""
                    } else nameActivity
                }
            }
        }

        val memberEmbed: EmbedBuilder = EmbedBuilder()
            .setTitle("Информация о " + member.user.asTag)
            .setColor(member.user.retrieveProfile().complete().accentColor)
            .setThumbnail(member.user.avatarUrl)
            .addField("Зарегистрировался", "<t:" + member.timeCreated.toZonedDateTime().toEpochSecond() + ":F>", false)
            .addField("Присоединился", "<t:" + member.timeJoined.toZonedDateTime().toEpochSecond() + ":F>", false)
            .addField("Сетевой статус", onlineStatus, true)
            .setFooter("Запросил ${e.author.name}")
        if(userActivityString.isNotEmpty()) memberEmbed.addField("Активность", userActivityString, true)
        if(userStatus.isNotEmpty()) memberEmbed.addField("Пользовательский статус", userStatus, false)

        e.channel.sendMessageEmbeds(memberEmbed.build()).queue()
    }


}