package me.avo.spottit.controller

import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.model.Configuration
import me.avo.spottit.model.Playlist
import me.avo.spottit.model.RedditCredentials
import me.avo.spottit.model.SpotifyCredentials
import me.avo.spottit.service.ElectronicSearchAlgorithm
import me.avo.spottit.service.RedditServiceImpl
import me.avo.spottit.service.SpotifyAuthService
import me.avo.spottit.service.SpotifyService
import me.avo.spottit.service.SpotifyHeadlessAuthService
import me.avo.spottit.util.TrackFilter
import me.avo.spottit.util.YamlConfigReader
import org.slf4j.LoggerFactory
import java.io.File

class DynamicPlaylistController(
    private val spotifyAuthService: SpotifyAuthService,
    private val spotifyService: SpotifyService,
    private val redditCredentials: RedditCredentials,
    private val client: SpotifyHeadlessAuthService
) {

    fun updatePlaylists(configPath: String) {
        val configuration = YamlConfigReader.read(File(configPath).readText())
        authenticate(configuration)
        configuration.playlists.forEach {
            processPlaylist(configuration, it)
        }
    }

    private fun authenticate(configuration: Configuration) {
        val uri = spotifyAuthService.getRedirectUri().toString()
        val spotifyCredentials = SpotifyCredentials(configuration.spotifyUser, configuration.spotifyPass, uri)
        val authCode = client.getAuthCode(spotifyCredentials)
        spotifyAuthService.grantAccess(authCode)
    }

    private fun processPlaylist(configuration: Configuration, playlist: Playlist) {
        logger.info("Processing playlist ${playlist.id}")
        val redditService = RedditServiceImpl(playlist, configuration.flairsToExclude, redditCredentials)
        val foundTracks = mutableListOf<Track>()

        val searchAlgorithm = ElectronicSearchAlgorithm(TrackFilter(configuration))

        while (foundTracks.size < playlist.maxSize && !redditService.isDone) redditService
            .getTracks()
            .let { spotifyService.findTracks(it, searchAlgorithm) }
            .also { redditService.update(it.size) }
            .mapTo(foundTracks) { it }
            .also { Thread.sleep(250) }

        spotifyService.updatePlaylist(foundTracks, playlist.userId, playlist.id, playlist.maxSize)
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

}