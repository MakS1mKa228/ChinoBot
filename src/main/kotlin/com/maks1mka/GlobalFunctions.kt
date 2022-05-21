package com.maks1mka

import com.github.tsohr.JSONObject
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun getPrefix(): String {
    return "."
}

fun getOwnerId() : String {
    return "598151504867753996"
}

fun getGuildForEmoji():String {
    return "900129288366481439"
}

fun getTimestamp(milliseconds: Long): String {
    val seconds: Int = ((milliseconds / 1000) % 60).toInt()
    val minutes: Int = (((milliseconds / (1000 * 60)) % 60)).toInt()
    val hours: Int   = (((milliseconds / (1000 * 60 * 60)) % 24)).toInt()

    return if (hours > 0)
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    else
        String.format("%02d:%02d", minutes, seconds)
}

