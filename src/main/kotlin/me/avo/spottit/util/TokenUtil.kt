package me.avo.spottit.util

import java.io.File

object TokenUtil {

    val accessTokenFile = File(System.getenv("ACCESS_TOKEN_FILE") ?: "access-token")

    val refreshTokenFile = File(System.getenv("REFRESH_TOKEN_FILE") ?: "refresh-token")

}