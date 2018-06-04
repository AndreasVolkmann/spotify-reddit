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
import me.avo.spottit.model.TagFilter
import me.avo.spottit.util.openUrlInBrowser
import net.dean.jraw.models.SubredditSort
import net.dean.jraw.models.TimePeriod
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

@Disabled
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
        val accessToken =
            "AQAmI_fU50XDxtJVSeRtFPK05lK8fXqVEMF7-YlUqe8QoxSTOEi6HkSsWwSheHJaUEstcKnAYAnKZfwhrCCl9PKR73AVU4PFE6xsHD2g5MrgJuZhcVQMB-v-lza-jm2GUqahrDrxoZJ3IZj9rOIOuy1t64YUjxW4lAIiGppghmc5lXV4la0aNahwg62EQhrNoPdJ1Sk0mABYzk4B-LYqDX_rEzULnHZ3Ttqso-K-6VUuuGZhelXXl5XjaBJO6qzrONXaENvdIqxH2GXwpXT8tBZ-ZvA1O3d67hohP10nomU8qvzT"
        val refreshToken =
            "AQCbUZX9XRExBTerHlXZhZs0ia84aFdq6Nje8EV9OGR3Btq00_5RBej11B9f453XtGWvBKzvoH3ZV_K8yr3dnuyfWYUDFSseMNvMmj4Zjc05ejBLMMZuO2sss3uBWqh1ZTOEVcevLfusknieV_QAsT680Ug0XlEDjZV-5RSv1Tm7I7EgpzvZDUnm7kPVeSQH4_EOzl1TsVBbXFUG902AGicJ6UBNi8nqnVMwEzQQGoViQQciI8wC5innHoYZpVx4XrRVKVfNQtVX"
        render(Templates.auth(accessToken, refreshToken))
    }

    @Test fun test() {
        val playlists = listOf(
            Playlist(
                id = "93tu39t3903tj",
                userId = "h378r3hr38",
                subreddit = "trance",
                timePeriod = TimePeriod.ALL,
                sort = SubredditSort.TOP,
                maxSize = 20,
                minimumUpvotes = 5,
                isStrictMix = false,
                tagFilter = TagFilter(
                    listOf(),
                    listOf(),
                    listOf(),
                    listOf()
                ),
                isPrivate = true
            ),
            Playlist(
                id = "hjfh373hf29",
                userId = "h378r3hr38",
                subreddit = "trance",
                timePeriod = TimePeriod.WEEK,
                sort = SubredditSort.TOP,
                maxSize = 10,
                minimumUpvotes = 10,
                isStrictMix = true,
                tagFilter = TagFilter(
                    includeExact = listOf("Edit", "Radio"),
                    include = listOf("FRESH"),
                    excludeExact = listOf("Album"),
                    exclude = listOf("video")
                ),
                isPrivate = false
            )
        )
        render(FreeMarkerContent("test.ftl", mapOf("playlists" to playlists), "e"))
    }

    @Test fun checkout() {
        class SimpleTrack(val id: String, val artist: String, val name: String)
        class Results(val total: Int, val tracks: List<SimpleTrack>) {
            constructor(tracks: List<SimpleTrack>) : this(tracks.size, tracks)
        }

        val tracksToAdd = listOf(
            SimpleTrack("1", "John Maus", "Sensitive Recollections"),
            SimpleTrack("2", "Adele", "Hometown Glory (High Contrast Remix)")
        )

        val tracksToDelete = listOf(
            SimpleTrack("3", "Luke Bond", "U"),
            SimpleTrack("4", "ARTY", "Rain (ASOT 858)")
        )

        val resultsAdd = Results(tracksToAdd)
        val resultsDel = Results(tracksToDelete)
        render(
            FreeMarkerContent(
                "checkout.ftl", mapOf(
                    "resultsAdd" to resultsAdd,
                    "resultsDel" to resultsDel
                ), "e"
            )
        )
    }

}