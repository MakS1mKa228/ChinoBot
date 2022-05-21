package com.maks1mka.commands


import com.maks1mka.getOwnerId
import com.maks1mka.getPrefix
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import javax.script.ScriptException

class EvalCommand: BaseCommand() {
    override val name: String = "Eval"
    override val aliases: List<String> = listOf("eval", "e")
    override val desc: String = "З"
    private var engine: ScriptEngine? = null
    init {
        engine = ScriptEngineManager().getEngineByName("nashorn")
        try {
            engine?.eval(
                "var imports = new JavaImporter(" +
                        "java.io," +
                        "java.lang," +
                        "java.util," +
                        "Packages.net.dv8tion.jda.api," +
                        "Packages.net.dv8tion.jda.api.entities," +
                        "Packages.net.dv8tion.jda.api.entities.impl," +
                        "Packages.net.dv8tion.jda.api.managers," +
                        "Packages.net.dv8tion.jda.api.managers.impl," +
                        "Packages.net.dv8tion.jda.api.utils);"
            )
        } catch (e: ScriptException) {
            e.printStackTrace()
        }
    }
    override fun onExecute(e: MessageReceivedEvent, name: String, args: List<String>) {

        if (e.author.id != getOwnerId())
            return
        try {
            engine?.put("event", e)
            engine?.put("message", e.message)
            engine?.put("channel", e.channel)
            engine?.put("args", args)
            engine?.put("api", e.jda)
            if (e.isFromType(ChannelType.TEXT)) {
                engine?.put("guild", e.guild)
                engine?.put("member", e.member)
            }
            e.jda.getVoiceChannelById("")?.members?.get(0)
             val out: Any? = engine?.eval(
                    "(function() {" +
                            "with (imports) {" +
                            " return " + e.message.contentRaw.substring((getPrefix() + name).length) +
                            "}" +
                            "})();")
            e.channel.sendMessage(out?.toString() ?: "Выполнено без ошибок.").queue()
        }
        catch (e1: Exception)
        {
            e.channel.sendMessage(e1.message.toString()).queue()
        }
    }

}