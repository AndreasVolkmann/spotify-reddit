package me.avo.spottit.service.spotify

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.SpotifyHttpManager
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials
import me.avo.spottit.util.RetrySupport
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI

class SpotifyAuthServiceImpl(
    override val clientId: String,
    override val clientSecret: String,
    override val redirectUri: String
) : SpotifyAuthService, RetrySupport {

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

    override fun getRedirectUri(scopes: Iterable<String>): URI = buildClientApi()
        .authorizationCodeUri()
        .scope(scopes.joinToString(",")) // comma separated String
        .execute()

    private fun getAccessToken(code: String): AuthorizationCodeCredentials =
        buildClientApi().authorizationCode(code).build().execute()

    private fun buildSpotifyApi(accessToken: String): SpotifyApi = SpotifyApi.Builder()
        .setAccessToken(accessToken)
        .build()

    private fun buildClientApi(): SpotifyApi = SpotifyApi.Builder()
        .setClientId(clientId)
        .setClientSecret(clientSecret)
        .setRedirectUri(SpotifyHttpManager.makeUri(redirectUri))
        .build()!!

    override val logger: Logger = LoggerFactory.getLogger(this::class.java)
}
