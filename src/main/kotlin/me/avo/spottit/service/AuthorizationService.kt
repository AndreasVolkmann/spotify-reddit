package me.avo.spottit.service

import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials
import me.avo.spottit.model.Configuration

interface AuthorizationService {

    fun authorize(configuration: Configuration)

    fun writeCredentials(credentials: AuthorizationCodeCredentials)

    fun getRequiredScopes(configuration: Configuration): Iterable<String>
}
