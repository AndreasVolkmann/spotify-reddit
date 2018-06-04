package me.avo.spottit.controller

import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials
import me.avo.spottit.model.Configuration
import me.avo.spottit.model.Playlist
import me.avo.spottit.util.TokenUtil

interface AuthorizationController {

    fun authorize(configuration: Configuration)

    fun writeCredentials(credentials: AuthorizationCodeCredentials) {
        TokenUtil.accessTokenFile.writeText(credentials.accessToken)
        TokenUtil.refreshTokenFile.writeText(credentials.refreshToken)
    }

    fun getRequiredScopes(configuration: Configuration): Iterable<String> {
        //val (private, public) = configuration.playlists.partition(Playlist::isPrivate)
        val private = if (configuration.playlists.any(Playlist::isPrivate)) "playlist-modify-private" else null
        val public = if (configuration.playlists.any { it.isPrivate.not() }) "playlist-modify-public" else null
        return listOfNotNull(private, public)
    }

}