package me.avo.spottit.service

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.SpotifyHttpManager
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials
import java.net.URI

interface SpotifyAuthService {

    val clientId: String
    val clientSecret: String
    val redirectUri: String

    var accessToken: String
    var refreshToken: String

    fun grantAccess(authCode: String): AuthorizationCodeCredentials

    fun refresh(): String

    fun getSpotifyApi(): SpotifyApi

    fun buildSpotifyApi(accessToken: String): SpotifyApi = SpotifyApi.Builder()
        .setAccessToken(accessToken)
        .build()

    fun buildClientApi(): SpotifyApi = SpotifyApi.Builder()
        .setClientId(clientId)
        .setClientSecret(clientSecret)
        .setRedirectUri(SpotifyHttpManager.makeUri(redirectUri))
        .build()!!

    fun getAccessToken(code: String): AuthorizationCodeCredentials =
        buildClientApi().authorizationCode(code).build().execute()

    fun getRedirectUri(scopes: Iterable<String>): URI = buildClientApi()
        .authorizationCodeUri()
        .scope(scopes.joinToString(",")) // comma separated String
        .build()
        .execute()

}