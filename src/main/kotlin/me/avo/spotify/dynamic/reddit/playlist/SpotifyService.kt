package me.avo.spotify.dynamic.reddit.playlist

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.SpotifyHttpManager
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials
import java.net.URI

class SpotifyService(
    private val clientId: String,
    private val clientSecret: String,
    private val redirectUri: String,
    private val userId: String,
    private val playlistId: String
) {

    private lateinit var accesstoken: String

    fun updatePlaylist(authCode: String?) {
        val accesstoken = when {
            ::accesstoken.isInitialized -> accesstoken
            authCode != null -> getAccessToken(authCode).apply {
                println("Access token expires in $expiresIn seconds")
                println("Access scope: $scope")
            }.accessToken
            else -> throw IllegalStateException("Access Token is not yet initialized")
        }
        this.accesstoken = accesstoken

        buildSpotifyApi(accesstoken).run {
            val playlist = getPlaylist(userId, playlistId).build().execute()
            val tracks = getPlaylistsTracks(userId, playlistId).build().execute()
            println(playlist.name)
            println(tracks.total)
            tracks.items.forEach {
                println(it.track.name)
            }

        }
    }

    fun getRedirectUri(): URI = buildClientApi()
        .authorizationCodeUri()
        .scope("playlist-modify-public,playlist-read-collaborative,playlist-read-private") // comma separated String
        .build()
        .execute()

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

}