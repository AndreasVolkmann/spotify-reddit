package me.avo.spotify.dynamic.reddit.playlist.server

import io.ktor.http.HttpStatusCode

class StatusException(val status: HttpStatusCode, override val message: String?): Exception()