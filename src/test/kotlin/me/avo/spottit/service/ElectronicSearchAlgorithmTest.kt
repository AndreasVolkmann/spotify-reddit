package me.avo.spottit.service

import com.github.salomonbrys.kodein.instance
import me.avo.spottit.config.kodein
import me.avo.spottit.controller.TokenRefreshController
import me.avo.spottit.redditTrack
import me.avo.spottit.util.TrackFilter
import me.avo.spottit.util.YamlConfigReader
import me.avo.spottit.util.format
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class ElectronicSearchAlgorithmTest {

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