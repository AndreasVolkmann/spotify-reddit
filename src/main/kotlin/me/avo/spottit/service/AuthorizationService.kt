package me.avo.spottit.service

import me.avo.spottit.model.Configuration

interface AuthorizationService {

    fun authorize(configuration: Configuration)

    fun getRequiredScopes(configuration: Configuration): Iterable<String>
}
