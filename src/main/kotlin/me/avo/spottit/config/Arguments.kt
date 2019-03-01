package me.avo.spottit.config

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.argument
import java.io.File

object Arguments : Arkenv("Spottit", propertiesFile = "application.properties") {

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
        defaultValue = { 5000 }
    }

    val refreshTokenFile: File by argument("--refresh-token-file") {
        defaultValue = { File("refresh-token") }
        mapping = ::File
    }

    val refreshToken: String by argument("--refresh_token") {
        defaultValue = { refreshTokenFile.readText().trim() }
    }

    val editDistance: Int by argument("--edit-distance") {
        defaultValue = { 15 }
    }

    val spotifyClientId: String by argument("--spotify-client-id")
    val spotifyClientSecret: String by argument("--spotify-client-secret")
    val redirectUri: String by argument("--redirect-uri")

    val redditClientId: String by argument("--reddit-client-id")
    val redditClientSecret: String by argument("--reddit-client-secret")
    val deviceName: String by argument("--device-name")
}
