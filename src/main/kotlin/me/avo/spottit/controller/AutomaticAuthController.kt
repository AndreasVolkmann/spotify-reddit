package me.avo.spottit.controller

import me.avo.spottit.model.Configuration
import me.avo.spottit.model.SpotifyCredentials
import me.avo.spottit.service.SpotifyAuthService
import me.avo.spottit.service.SpotifyHeadlessAuthService

class AutomaticAuthController(
    private val spotifyAuthService: SpotifyAuthService,
    private val client: SpotifyHeadlessAuthService
) : AuthorizationController {

    override fun authorize(configuration: Configuration) {
        val scopes = getRequiredScopes(configuration)
        val uri = spotifyAuthService.getRedirectUri(scopes).toString()
        val spotifyCredentials = getSpotifyCredentials(uri)
        val authCode = client.getAuthCode(spotifyCredentials)
        val credentials = spotifyAuthService.grantAccess(authCode)
        writeCredentials(credentials)
    }

    private fun getSpotifyCredentials(uri: String): SpotifyCredentials = SpotifyCredentials(
        user = System.getenv("SPOTIFY_USER"),
        pass = System.getenv("SPOTIFY_PASS"),
        url = uri
    )

}