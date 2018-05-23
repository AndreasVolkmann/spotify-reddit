package me.avo.spottit.server

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.freemarker.FreeMarker
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import me.avo.spottit.model.Playlist
import me.avo.spottit.util.openUrlInBrowser
import net.dean.jraw.models.SubredditSort
import net.dean.jraw.models.TimePeriod
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

internal class TemplatesTest {

    private fun render(content: FreeMarkerContent) {
        val port = 5001
        val url = "http://localhost:$port/"
        val server = embeddedServer(Netty, port) {
            install(FreeMarker) {
                setupFreemarker()
            }
            install(Routing) {
                get {
                    call.respond(content)
                }
            }
        }.start(false)
        openUrlInBrowser(url)
        Thread.sleep(3000)
        server.stop(2L, 2L, TimeUnit.SECONDS)
    }

    @Test fun auth() {
        render(Templates.auth())
    }

    @Test fun test() {
        val playlists = listOf(
            Playlist(
                id = "93tu39t3903tj",
                userId = "h378r3hr38",
                subreddit = "trance",
                timePeriod = TimePeriod.ALL,
                sort = SubredditSort.TOP,
                maxSize = 20
            ),
            Playlist(
                id = "hjfh373hf29",
                userId = "h378r3hr38",
                subreddit = "trance",
                timePeriod = TimePeriod.WEEK,
                sort = SubredditSort.TOP,
                maxSize = 10
            )
        )
        render(FreeMarkerContent("test.ftl", mapOf("playlists" to playlists), "e"))
    }

    @Test fun checkout() {
        class SimpleTrack(val id: String, val artist: String, val name: String)
        class Results(val total: Int, val tracks: List<SimpleTrack>) {
            constructor(tracks: List<SimpleTrack>): this(tracks.size, tracks)
        }

        val tracksToAdd = listOf(
            SimpleTrack("1", "John Maus", "Sensitive Recollections"),
            SimpleTrack("2", "Adele", "Hometown Glory (High Contrast Remix)")
        )

        val resultsAdd = Results(tracksToAdd)
        render(FreeMarkerContent("checkout.ftl", mapOf("resultsAdd" to resultsAdd), "e"))
    }

}