package com.maks1mka

import com.maks1mka.commands.*
import com.maks1mka.commands.music.*
import net.dv8tion.jda.api.JDABuilder

class CommandHandler(builder: JDABuilder) {
    private val builder: JDABuilder

    init {
        this.builder = builder
    }

    fun initCommands(): JDABuilder {

        with(this.builder) {
            addEventListeners(

                //DEV
                PingCommand(),
                EvalCommand(),

                //INFO
                AvatarCommand(),
                BannerCommand(),
                UserProfileCommand(),

                //FUN
                ActionCommand(),
                AnimeSceneSearchCommand(),
                QuoteCommand(),

                //MUSIC
                JoinCommand(),
                LeaveCommand(),
                PlayCommand(),
                ClearQueueCommand(),
                SkipCommand(),
                PauseCommand(),
                RepeatCommand(),
            )
        }
        return this.builder
    }
}