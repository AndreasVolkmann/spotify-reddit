package me.avo.spottit.service

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials

class SpotifyAuthServiceImpl(
    override val clientId: String,
    override val clientSecret: String,
    override val redirectUri: String
) : SpotifyAuthService {

    private lateinit var accesstoken: String
    private lateinit var refreshToken: String
    private var expiresInSeconds: Int = 0

    override fun grantAccess(authCode: String) {
        val auth = getAccessToken(authCode)
        updateCredentials(auth, true)
    }

    override fun refresh() {
        val auth = SpotifyApi.Builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setRefreshToken(refreshToken)
            .build()
            .authorizationCodeRefresh()
            .build()
            .execute()
        updateCredentials(auth, false)
    }

    override fun getSpotifyApi(): SpotifyApi = buildSpotifyApi(accesstoken)

    private fun updateCredentials(auth: AuthorizationCodeCredentials, setRefresh: Boolean) {
        accesstoken = auth.accessToken
        expiresInSeconds = auth.expiresIn
        if (setRefresh) {
            refreshToken = auth.refreshToken
        }
        println("Acquired new access ${auth.tokenType} token, which expires in $expiresInSeconds seconds. $refreshToken")
    }

}