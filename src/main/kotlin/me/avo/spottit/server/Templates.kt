package me.avo.spottit.server

import io.ktor.freemarker.FreeMarkerContent

object Templates {

    fun auth() = FreeMarkerContent("auth.ftl", null, "e")


}