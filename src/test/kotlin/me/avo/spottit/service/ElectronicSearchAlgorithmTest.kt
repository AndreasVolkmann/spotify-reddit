package me.avo.spottit.service

import me.avo.spottit.RequiresToken
import me.avo.spottit.TestKodeinAware
import me.avo.spottit.makeConfig
import me.avo.spottit.model.Configuration
import me.avo.spottit.model.DateFilter
import me.avo.spottit.model.Playlist
import me.avo.spottit.redditTrack
import me.avo.spottit.util.*
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable
import org.kodein.di.generic.factory2
import org.kodein.di.generic.instance
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisabledIfEnvironmentVariable(named = "DISABLE_NETWORK_TESTS", matches = "1")
internal class ElectronicSearchAlgorithmTest : RequiresToken, TestKodeinAware {

    private val spotifyAuthService: SpotifyAuthService by instance()
    private val getTrackFilter: (Configuration, Playlist) -> TrackFilter by factory2()

    @Test fun `getTrack by id`() {
        val api = spotifyAuthService.getSpotifyApi()
        val id = "7ICUbPlGOPSBA1oLgudcV4"
        val url = "https://open.spotify.com/track/$id?si=FKQkuYhkQii1g_TTsHPI8g"
        val urls = listOf(
            "https://open.spotify.com/track/4lFxQyVeTYkM010VUtCp6o?si=SrEvWeOzRtK02n5bVL8Slw",
            "https://open.spotify.com/track/63pvV3o0aeVrwRWdtx4wOX?si=x9klrbFJRkWfZl7o2VB3vw",
            "https://open.spotify.com/track/3DPFmwFtV5ElQaTniLOdgk?si=NScDaJS4RpGzoFn2pzdoGQ"
        )
        val track = SubmissionParser.parse("[FRESH] Joy Again - Nobody Knows", null, url, Date())
        track.isSpotifyTrack shouldBe true

        val tracks = urls.map { redditTrack("", "", url = it) } + track

        val config = makeConfig(
            DateFilter(
                startingFrom = parseDateString("2012-04-04"), maxDistance = null
            )
        )
        val trackFilter = getTrackFilter(config, config.playlists.first())
        val alg = ElectronicSearchAlgorithm(api, trackFilter)

        val results = alg.searchForTracks(tracks)

        results
            .find { it.name == "Nobody Knows (2018)" }
            .shouldNotBeNull()
            .also {
                it.artistString() shouldBeEqualTo "Joy Again"
                it.id shouldBeEqualTo id
            }

        results.forEach {
            println(it.format())
            alg.getAlbumForTrack(it).let(trackFilter::checkTrackAgeByAlbum).let(::println)
        }
    }

}
