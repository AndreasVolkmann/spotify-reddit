package me.avo.spottit.service

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials
import me.avo.spottit.util.TokenUtil

class SpotifyAuthServiceImpl(
    override val clientId: String,
    override val clientSecret: String,
    override val redirectUri: String
) : SpotifyAuthService {

    private lateinit var accessToken: String
    private lateinit var refreshToken: String
    private var expiresInSeconds: Int = 0

    override fun grantAccess(authCode: String): AuthorizationCodeCredentials {
        val auth = getAccessToken(authCode)
        updateCredentials(auth, true)
        return auth
    }

    override fun loadCredentials() {
        val accessToken = TokenUtil.accessTokenFile.readText()
        val refreshToken = TokenUtil.refreshTokenFile.readText()

        if (accessToken.isBlank() && refreshToken.isBlank()) {
            throw IllegalArgumentException("Both access and refresh token are empty")
        }

        if (accessToken.isBlank()) {
            // TODO try to refresh
        }

        if (refreshToken.isBlank()) {
            // TODO try to just use access token
        }

        this.accessToken = accessToken
        this.refreshToken = refreshToken
    }

    override fun refresh(): String {
        val auth = SpotifyApi.Builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setRefreshToken(refreshToken)
            .build()
            .authorizationCodeRefresh()
            .build()
            .execute()
        updateCredentials(auth, false)
        return auth.accessToken
    }

    override fun grantRefresh(refreshToken: String) {
        this.refreshToken = refreshToken
    }

    override fun getSpotifyApi(): SpotifyApi = buildSpotifyApi(accessToken)

    private fun updateCredentials(auth: AuthorizationCodeCredentials, setRefresh: Boolean) {
        accessToken = auth.accessToken
        expiresInSeconds = auth.expiresIn
        if (setRefresh) {
            refreshToken = auth.refreshToken
        }
        //println("Acquired new access ${auth.tokenType} token, which expires in $expiresInSeconds seconds. $refreshToken")
    }

}