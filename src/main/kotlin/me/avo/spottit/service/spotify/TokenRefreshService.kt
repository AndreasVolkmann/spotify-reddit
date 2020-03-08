package me.avo.spottit.service.spotify

import me.avo.spottit.config.Arguments
import org.slf4j.LoggerFactory

class TokenRefreshService(
    private val spotifyAuthService: SpotifyAuthService
) {

    fun refresh(): String {
        logger.info("Refreshing Spotify access token")
        val refreshToken = Arguments.refreshToken
        spotifyAuthService.refreshToken = refreshToken

        val newAccessToken = spotifyAuthService.refresh()
        spotifyAuthService.accessToken = newAccessToken
        logger.info("The access token has been refreshed")
        return newAccessToken
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

}
