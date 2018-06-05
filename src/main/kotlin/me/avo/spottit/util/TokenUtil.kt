package me.avo.spottit.util

import java.io.File

object TokenUtil {

    val refreshTokenFile = File(System.getenv("REFRESH_TOKEN_FILE") ?: "refresh-token")

    fun getRefreshToken() = System.getenv("REFRESH_TOKEN") ?: refreshTokenFile.readText().trim()

}