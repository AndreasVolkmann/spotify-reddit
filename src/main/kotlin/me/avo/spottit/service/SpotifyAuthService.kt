package me.avo.spottit.service

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.SpotifyHttpManager
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials
import java.net.URI

interface SpotifyAuthService {

    val clientId: String
    val clientSecret: String
    val redirectUri: String

    fun grantAccess(authCode: String)

    fun refresh()

    fun getSpotifyApi(): SpotifyApi

    fun buildSpotifyApi(accesstoken: String): SpotifyApi = SpotifyApi.Builder()
        .setAccessToken(accesstoken)
        .build()

    fun buildClientApi(): SpotifyApi = SpotifyApi.Builder()
        .setClientId(clientId)
        .setClientSecret(clientSecret)
        .setRedirectUri(SpotifyHttpManager.makeUri(redirectUri))
        .build()!!

    fun getAccessToken(code: String): AuthorizationCodeCredentials =
        buildClientApi().authorizationCode(code).build().execute()

    fun getRedirectUri(): URI = buildClientApi()
        .authorizationCodeUri()
        .scope("playlist-modify-public,playlist-read-collaborative,playlist-read-private") // comma separated String
        .build()
        .execute()

}