package me.avo.spottit.service

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials

class SpotifyAuthServiceImpl(
    override val clientId: String,
    override val clientSecret: String,
    override val redirectUri: String
) : SpotifyAuthService {

    override lateinit var accessToken: String
    override lateinit var refreshToken: String
    private var expiresInSeconds: Int = 0

    override fun grantAccess(authCode: String): AuthorizationCodeCredentials {
        val auth = getAccessToken(authCode)
        updateCredentials(auth, true)
        return auth
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

    override fun getSpotifyApi(): SpotifyApi = buildSpotifyApi(accessToken)

    private fun updateCredentials(auth: AuthorizationCodeCredentials, setRefresh: Boolean) {
        accessToken = auth.accessToken
        expiresInSeconds = auth.expiresIn
        if (setRefresh) {
            refreshToken = auth.refreshToken
        }
    }

}