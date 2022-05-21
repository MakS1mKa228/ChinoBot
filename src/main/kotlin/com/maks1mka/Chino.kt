package com.maks1mka

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag



fun main(args: Array<String>) {

    val token: String = args[0]
    val intents: Collection<GatewayIntent> = listOf(
        GatewayIntent.GUILD_MESSAGES,
        GatewayIntent.GUILD_MEMBERS,
        GatewayIntent.GUILD_PRESENCES,
        GatewayIntent.GUILD_MESSAGE_TYPING,
        GatewayIntent.GUILD_VOICE_STATES,
        GatewayIntent.GUILD_EMOJIS
    )
    val cache: Collection<CacheFlag> = listOf(
        CacheFlag.ACTIVITY,
        CacheFlag.CLIENT_STATUS,
        CacheFlag.ONLINE_STATUS,
        CacheFlag.VOICE_STATE,
        CacheFlag.MEMBER_OVERRIDES,
        CacheFlag.EMOTE
    )
    val builder: JDABuilder = JDABuilder.create(token, intents).enableCache(cache)
    with(builder) {
        setActivity(Activity.listening("NightCore"))
    }
    CommandHandler(builder).initCommands().build()
}


