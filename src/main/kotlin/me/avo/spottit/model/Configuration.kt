package me.avo.spottit.model

data class Configuration(
    val userId: String,
    val playlists: List<Playlist>,
    val flairsToExclude: List<String>
)