package me.avo.spottit.service

import com.github.salomonbrys.kodein.instance
import me.avo.spottit.RequiresToken
import me.avo.spottit.config.kodein
import me.avo.spottit.getTestConfig
import me.avo.spottit.util.SubmissionParser
import me.avo.spottit.util.TrackFilter
import me.avo.spottit.util.artistString
import me.avo.spottit.util.format
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ElectronicSearchAlgorithmTest : RequiresToken {

    @Test fun `getTrack by id`() {
        val title = "[FRESH] Joy Again - Nobody Knows"
        val id = "7ICUbPlGOPSBA1oLgudcV4"
        val url = "https://open.spotify.com/track/$id?si=FKQkuYhkQii1g_TTsHPI8g"
        val track = SubmissionParser.parse(title, null, url, Date())
        track.isSpotifyTrack shouldBe true

        val config = getTestConfig()
        val spotifyAuthService: SpotifyAuthService = kodein.instance()
        val alg = ElectronicSearchAlgorithm(
            spotifyAuthService.getSpotifyApi(),
            TrackFilter(config, config.playlists.first())
        )

        val results = alg.searchForTracks(tracks = listOf(track))

        results.first().let {
            println(it.format())
            it.artistString() shouldBeEqualTo "Joy Again"
            it.name shouldBeEqualTo "Nobody Knows (2018)"
            it.id shouldBeEqualTo id
            spotifyAuthService.getSpotifyApi().getAlbum(it.album.id).build().execute().let {
                println(it.releaseDate)
                println(it.releaseDatePrecision)
            }
        }
    }

}