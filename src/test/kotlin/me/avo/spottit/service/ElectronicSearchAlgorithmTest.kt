package me.avo.spottit.service

import com.github.salomonbrys.kodein.instance
import me.avo.spottit.RequiresToken
import me.avo.spottit.config.kodein
import me.avo.spottit.controller.TokenRefreshController
import me.avo.spottit.getTestConfig
import me.avo.spottit.redditTrack
import me.avo.spottit.util.*
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ElectronicSearchAlgorithmTest : RequiresToken {

    @Test fun `getTrack by id`() {
        val title = "[FRESH] Joy Again - Nobody Knows"
        val id = "7ICUbPlGOPSBA1oLgudcV4"
        val url = "https://open.spotify.com/track/$id?si=FKQkuYhkQii1g_TTsHPI8g"
        val track = SubmissionParser.parse(title, null, url)
        track.isSpotifyTrack shouldBe true

        val config = getTestConfig()
        val alg = ElectronicSearchAlgorithm(TrackFilter(config, config.playlists.first()))

        val spotifyAuthService: SpotifyAuthService = kodein.instance()
        val results = alg.searchForTracks(
            spotifyApi = spotifyAuthService.getSpotifyApi(),
            tracks = listOf(track)
        )

        results.first().let {
            println(it.format())
            it.artistString() shouldBeEqualTo "Joy Again"
            it.name shouldBeEqualTo "Nobody Knows (2018)"
            it.id shouldBeEqualTo id
        }
    }

    @Disabled
    @Test fun findTrack() {
        val yaml = this::class.java.classLoader.getResource("actual_config.yml").readText()
        val config = YamlConfigReader.read(yaml)
        val playlist = config.playlists.first()
        val alg = ElectronicSearchAlgorithm(TrackFilter(config, playlist))

        kodein.instance<TokenRefreshController>().refresh()
        val spotifyAuthService: SpotifyAuthService = kodein.instance()

        val results = alg.searchForTracks(
            spotifyApi = spotifyAuthService.getSpotifyApi(),
            tracks = listOf(
                //redditTrack("Daft Punk", "Face To Face")
                redditTrack("The Weeknd", "I Feel It Coming", "Daft Punk")
            )
        )

        results.forEach { println(it.format()) }

    }

}