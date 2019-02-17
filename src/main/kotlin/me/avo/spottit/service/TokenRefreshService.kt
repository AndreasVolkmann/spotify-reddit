package me.avo.spottit.service

import me.avo.spottit.util.TokenUtil
import org.slf4j.LoggerFactory

class TokenRefreshService(
    private val spotifyAuthService: SpotifyAuthService
) {

    fun refresh(): String {
        logger.info("Refreshing Spotify access token")
        val refreshToken = TokenUtil.getRefreshToken()
        spotifyAuthService.refreshToken = refreshToken

        val newAccessToken = spotifyAuthService.refresh()
        spotifyAuthService.accessToken = newAccessToken
        logger.info("The access token has been refreshed")
        return newAccessToken
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

}
