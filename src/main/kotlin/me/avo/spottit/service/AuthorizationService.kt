package me.avo.spottit.service

import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials
import me.avo.spottit.config.Arguments
import me.avo.spottit.model.Configuration
import me.avo.spottit.model.Playlist
import me.avo.spottit.util.TokenUtil

interface AuthorizationService {

    fun authorize(configuration: Configuration, arguments: Arguments)

    fun writeCredentials(credentials: AuthorizationCodeCredentials) {
        TokenUtil.refreshTokenFile.writeText(credentials.refreshToken)
    }

    fun getRequiredScopes(configuration: Configuration): Iterable<String> {
        val private = if (configuration.playlists.any(Playlist::isPrivate)) "playlist-modify-private" else null
        val public = if (configuration.playlists.any { it.isPrivate.not() }) "playlist-modify-public" else null
        return listOfNotNull(private, public)
    }
}
