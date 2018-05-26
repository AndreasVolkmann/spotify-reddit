package me.avo.spottit.controller

import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials
import me.avo.spottit.util.TokenUtil

interface AuthorizationController {

    fun authorize()

    fun writeCredentials(credentials: AuthorizationCodeCredentials) {
        TokenUtil.accessTokenFile.writeText(credentials.accessToken)
        TokenUtil.refreshTokenFile.writeText(credentials.refreshToken)
    }

}