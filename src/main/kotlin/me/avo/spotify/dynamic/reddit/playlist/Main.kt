package me.avo.spotify.dynamic.reddit.playlist

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import java.util.*

fun main(args: Array<String>) {

    val spotifyService: SpotifyService = kodein.instance()
    val uri = spotifyService.getRedirectUri().toString().also(::println)

    val server = prepareServer(kodein).start(wait = false)

    //runClient(uri, server)
}

object Main

val kodein = Kodein {
    val props = Properties().apply {
        load(Main::class.java.classLoader.getResourceAsStream("application.properties"))
    }
    val clientId = props.getProperty("clientId")
    val clientSecret = props.getProperty("clientSecret")
    val redirectUri = props.getProperty("redirectUri")
    val userId = props.getProperty("userId")
    val playlistId = props.getProperty("playlistId")

    bind<SpotifyService>() with singleton {
        SpotifyService(
            clientId,
            clientSecret,
            redirectUri,
            userId,
            playlistId
        )
    }

}
