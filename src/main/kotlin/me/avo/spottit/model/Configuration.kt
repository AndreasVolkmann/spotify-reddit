package me.avo.spottit.model

data class Configuration(
    val playlists: List<Playlist>,
    val flairsToExclude: List<String>,
    /**
     * Minimum length of tracks in seconds to get added to the playlists
     */
    val minimumLength: Int = 0,

    val rateLimitInMs: Long,
    val schedule: Schedule
)
