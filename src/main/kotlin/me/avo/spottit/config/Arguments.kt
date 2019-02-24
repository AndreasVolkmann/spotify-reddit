package me.avo.spottit.config

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.argument
import java.io.File

object Arguments : Arkenv("Spottit") {

    val configPath: String by argument("-c", "--config") {
        description = "The path to your config.yml"
    }

    val manualAuth: Boolean by argument("-ma", "--manual-auth") {
        description = "Manually authorize the app to Spotify"
    }

    val doRefresh: Boolean by argument("-r", "--refresh") {
        description = "Refresh the Spotify access token"
    }

    val port: Int by argument("-p", "--port") {
        description = "The port that the authentication server will be exposed on"
        defaultValue = 5000
    }

    val refreshTokenFile: File by argument("--refresh-token-file") {
        defaultValue = File("refresh-token")
        mapping = ::File
    }

    val refreshToken: String by argument("--refresh_token") {
        defaultValue = refreshTokenFile.readText().trim()
    }
}
