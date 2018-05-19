package me.avo.spotify.dynamic.reddit.playlist.service

import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.toJsonArray
import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.SpotifyHttpManager
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials
import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spotify.dynamic.reddit.playlist.model.RedditTrack
import org.slf4j.LoggerFactory
import java.net.URI

class SpotifyService(
    private val clientId: String,
    private val clientSecret: String,
    private val redirectUri: String,
    private val userId: String,
    private val playlistId: String
) {

    private lateinit var accesstoken: String

    fun grantAccess(authCode: String?) {
        val accesstoken = when {
            ::accesstoken.isInitialized -> accesstoken
            authCode != null -> getAccessToken(authCode).apply {
                logger.debug("Access token expires in $expiresIn seconds")
                logger.debug("Access scope: $scope")
            }.accessToken
            else -> throw IllegalStateException("Access Token is not yet initialized")
        }
        this.accesstoken = accesstoken

        buildSpotifyApi(accesstoken).run {
            val playlist = getPlaylist(userId, playlistId).build().execute()
            //val tracks = getPlaylistsTracks(userId, playlistId).build().execute()
            logger.info(playlist.name)
            logger.info("Playlist contains ${playlist.tracks.total} tracks")
        }
    }

    fun getRedirectUri(): URI = buildClientApi()
        .authorizationCodeUri()
        .scope("playlist-modify-public,playlist-read-collaborative,playlist-read-private") // comma separated String
        .build()
        .execute()

    fun updatePlaylist(tracks: List<RedditTrack>) {
        logger.info("Updating Playlist")
        val api = buildSpotifyApi(accesstoken)

        val results = api.searchForTracks(tracks)

        api.clearPlaylist()

        val tracksToAdd = results.mapNotNull { (redditTrack, results) -> results.firstOrNull() }
        api.addTracks(tracksToAdd)
    }

    private fun SpotifyApi.searchForTracks(tracks: List<RedditTrack>): List<Pair<RedditTrack, Array<Track>>> = tracks
        .map { findTrack(it) }
        .onEach { Thread.sleep(250) }

    private fun SpotifyApi.findTrack(track: RedditTrack): Pair<RedditTrack, Array<Track>> {
        val query = listOf(track.artist, track.title, track.mix).joinToString(" ")
        logger.info("Searching for $query")
        val results = searchTracks(query).limit(10).offset(0).build().execute()
        val items = results.items
        items.firstOrNull()
            .let { "Found ${results.total} results. " + if (it != null) "Top 1: ${it.artists.joinToString { it.name }} ${it.name}" else "" }
            .let(::println)
        return track to items
    }

    private fun SpotifyApi.clearPlaylist() {
        logger.info("Clearing Playlist")
        val tracksToRemove = getPlaylistsTracks(userId, playlistId).build().execute().items
            .map { it.track.uri }
            .map { jsonObject("uri" to it) }
            .toJsonArray()
        removeTracksFromPlaylist(userId, playlistId, tracksToRemove).build().execute()
    }

    private fun SpotifyApi.addTracks(tracks: Collection<Track>) {
        addTracksToPlaylist(userId, playlistId, tracks.map { it.uri }.toTypedArray()).build().execute()
    }

    private fun getAccessToken(code: String): AuthorizationCodeCredentials =
        buildClientApi().authorizationCode(code).build().execute()

    private fun buildClientApi(): SpotifyApi = SpotifyApi.Builder()
        .setClientId(clientId)
        .setClientSecret(clientSecret)
        .setRedirectUri(SpotifyHttpManager.makeUri(redirectUri))
        .build()!!

    private fun buildSpotifyApi(accesstoken: String): SpotifyApi = SpotifyApi.Builder()
        .setAccessToken(accesstoken)
        .build()

    private val logger = LoggerFactory.getLogger(this::class.java)

}