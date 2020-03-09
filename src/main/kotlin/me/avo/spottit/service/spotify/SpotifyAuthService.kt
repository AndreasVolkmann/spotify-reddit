package me.avo.spottit.service.spotify

import com.wrapper.spotify.SpotifyApi
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

    fun getRedirectUri(scopes: Iterable<String>): URI
}
