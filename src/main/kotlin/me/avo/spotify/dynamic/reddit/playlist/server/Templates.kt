package me.avo.spotify.dynamic.reddit.playlist.server

import io.ktor.freemarker.FreeMarkerContent

object Templates {

    fun auth() = FreeMarkerContent("auth.ftl", null, "e")


}