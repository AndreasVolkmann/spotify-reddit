package me.avo.spottit.service

import me.avo.spottit.*
import me.avo.spottit.model.Configuration
import me.avo.spottit.model.DateFilter
import me.avo.spottit.model.Playlist
import me.avo.spottit.util.SubmissionParser.parse
import me.avo.spottit.util.TrackFilter
import me.avo.spottit.util.artistString
import me.avo.spottit.util.parseDateString
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable
import org.kodein.di.generic.factory2
import org.kodein.di.generic.instance
import strikt.api.expectThat
import strikt.assertions.isNull
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisabledIfEnvironmentVariable(named = "DISABLE_NETWORK_TESTS", matches = "1")
internal class ElectronicSearchAlgorithmTest : TestKodeinAware {

    private val spotifyAuthService: SpotifyAuthService by instance()
    private val getTrackFilter: (Configuration, Playlist) -> TrackFilter by factory2()
    private val api get() = spotifyAuthService.getSpotifyApi()
    private val trackFilter by lazy { getTrackFilter(config, config.playlists.first()) }
    private val config = makeConfig(
        DateFilter(startingFrom = parseDateString("2012-04-04"), maxDistance = null)
    )
    private val alg by lazy { ElectronicSearchAlgorithm(api, trackFilter) }

    @Test fun `getTrack by id`() {
        val id = "7ICUbPlGOPSBA1oLgudcV4"
        val url = "https://open.spotify.com/track/$id?si=FKQkuYhkQii1g_TTsHPI8g"
        val urls = listOf(
            "https://open.spotify.com/track/4lFxQyVeTYkM010VUtCp6o?si=SrEvWeOzRtK02n5bVL8Slw",
            "https://open.spotify.com/track/63pvV3o0aeVrwRWdtx4wOX?si=x9klrbFJRkWfZl7o2VB3vw",
            "https://open.spotify.com/track/3DPFmwFtV5ElQaTniLOdgk?si=NScDaJS4RpGzoFn2pzdoGQ"
        )
        val track = parse("[FRESH] Joy Again - Nobody Knows", null, url, Date())
        track.isSpotifyTrack shouldBe true

        val alg = ElectronicSearchAlgorithm(api, trackFilter)

        val tracks = urls.map { redditTrack("", "", url = it) } + track
        val results = alg.searchForTracks(tracks)

        results
            .find { it.name == "Nobody Knows (2018)" }
            .shouldNotBeNull()
            .also {
                it.artistString() shouldBeEqualTo "Joy Again"
                it.id shouldBeEqualTo id
            }

        results.forEach {
            alg.getAlbumForTrack(it).let(trackFilter::checkTrackAgeByAlbum).let(::println)
        }
    }

    private val spotifyTrack = track {
        setArtists(artist("Fonzeca-Caja de Pandora Project"))
        setName("All Your Love Is Not Enough")
    }
    private val redditTrack = parse("Project One - Love Is Not Enough", null, "https://clyp.it/yfjzvnhp", Date())

    @Test fun `single tracks results should be filtered`() {
        val result = alg.evaluateResults(redditTrack, arrayOf(spotifyTrack), 0)
        expectThat(result).isNull()
    }

    @Test fun `multiple tracks results should be filtered`() {
        val result = alg.evaluateResults(redditTrack, arrayOf(spotifyTrack, spotifyTrack), 0)
        expectThat(result).isNull()
    }
}
