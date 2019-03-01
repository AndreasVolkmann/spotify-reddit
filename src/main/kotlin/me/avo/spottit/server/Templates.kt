package me.avo.spottit.server

import io.ktor.freemarker.FreeMarkerContent

object Templates {

    fun auth(accesToken: String, refreshToken: String) =
        FreeMarkerContent("manual_auth.ftl", mapOf("accessToken" to accesToken, "refreshToken" to refreshToken), "e")
    
}
