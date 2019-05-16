package me.avo.spottit.config

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.argument
import com.apurebase.arkenv.feature.ProfileFeature
import java.io.File

object Arguments : Arkenv(programName = "Spottit", configuration = {
    install(ProfileFeature())
}) {

    val configPath: String by argument("-c", "--config") {
        description = "The path to your config.yml"
    }

    val useLists: List<String> by argument {
        description = "When defined will only update lists with the provided comma-separated ids"
        mapping = { it.split(',') }
        defaultValue = ::emptyList
    }

    val manualAuth: Boolean by argument("-ma") {
        description = "Manually authorize the app to Spotify"
    }

    val doRefresh: Boolean by argument("-r", "--refresh") {
        description = "Refresh the Spotify access token"
    }

    val port: Int by argument {
        description = "The port that the authentication server will be exposed on"
    }

    val refreshTokenFile: File by argument {
        defaultValue = { File("refresh-token") }
        mapping = ::File
    }

    val refreshToken: String by argument {
        defaultValue = { refreshTokenFile.readText().trim() }
    }

    val editDistance: Int by argument()

    val spotifyClientId: String by argument()
    val spotifyClientSecret: String by argument()
    val redirectUri: String by argument()

    val redditClientId: String by argument()
    val redditClientSecret: String by argument()
    val deviceName: String by argument()
    val redditMaxPage: Int by argument()
}
