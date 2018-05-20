package me.avo.spotify.dynamic.reddit.playlist.service

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.SpotifyHttpManager
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials
import java.net.URI

class SpotifyAuthService(
    private val clientId: String,
    private val clientSecret: String,
    private val redirectUri: String
) {

    private lateinit var accesstoken: String

    fun grantAccess(authCode: String?) {
        val accesstoken = when {
            ::accesstoken.isInitialized -> accesstoken
            authCode != null -> getAccessToken(authCode).accessToken
            else -> throw IllegalStateException("Access Token is not yet initialized")
        }
        this.accesstoken = accesstoken
    }

    fun getRedirectUri(): URI = buildClientApi()
        .authorizationCodeUri()
        .scope("playlist-modify-public,playlist-read-collaborative,playlist-read-private") // comma separated String
        .build()
        .execute()

    fun getSpotifyApi() = buildSpotifyApi(accesstoken)

    private fun buildSpotifyApi(accesstoken: String): SpotifyApi = SpotifyApi.Builder()
        .setAccessToken(accesstoken)
        .build()

    private fun buildClientApi(): SpotifyApi = SpotifyApi.Builder()
        .setClientId(clientId)
        .setClientSecret(clientSecret)
        .setRedirectUri(SpotifyHttpManager.makeUri(redirectUri))
        .build()!!

    private fun getAccessToken(code: String): AuthorizationCodeCredentials =
        buildClientApi().authorizationCode(code).build().execute()

}