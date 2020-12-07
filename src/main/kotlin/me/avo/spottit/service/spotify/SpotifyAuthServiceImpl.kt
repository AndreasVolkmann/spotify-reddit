package me.avo.spottit.service.spotify

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.SpotifyHttpManager
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials
import me.avo.spottit.config.Arguments
import me.avo.spottit.util.RetrySupport
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI

class SpotifyAuthServiceImpl(
    override val clientId: String,
    override val clientSecret: String,
    override val redirectUri: String
) : SpotifyAuthService, RetrySupport {

    private var isInitialized: Boolean = false
    private lateinit var accessToken: String
    private lateinit var refreshToken: String
    private var expiresInSeconds: Int = 0

    override fun grantAccess(authCode: String): AuthorizationCodeCredentials = getAccessToken(authCode)

    override fun refresh() {
        if (isInitialized) return
        val auth = SpotifyApi.Builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setRefreshToken(getRefreshToken())
            .build()
            .authorizationCodeRefresh()
            .execute()
        updateCredentials(auth)
    }

    override fun getSpotifyApi(): SpotifyApi {
        refresh()
        return buildSpotifyApi(accessToken)
    }

    private fun updateCredentials(auth: AuthorizationCodeCredentials) {
        accessToken = auth.accessToken
        expiresInSeconds = auth.expiresIn
        refreshToken = auth.refreshToken ?: getRefreshToken()
        isInitialized = true
    }

    override fun getRedirectUri(scopes: Iterable<String>): URI = buildClientApi()
        .authorizationCodeUri()
        .scope(scopes.joinToString(",")) // comma separated String
        .execute()

    private fun getAccessToken(code: String): AuthorizationCodeCredentials =
        buildClientApi().authorizationCode(code).execute()

    private fun buildSpotifyApi(accessToken: String): SpotifyApi = SpotifyApi.Builder()
        .setAccessToken(accessToken)
        .build()

    private fun buildClientApi(): SpotifyApi = SpotifyApi.Builder()
        .setClientId(clientId)
        .setClientSecret(clientSecret)
        .setRedirectUri(SpotifyHttpManager.makeUri(redirectUri))
        .build()

    private fun getRefreshToken(): String = Arguments.getRefreshToken()

    override val logger: Logger = LoggerFactory.getLogger(this::class.java)
}
