package me.avo.spotify.dynamic.reddit.playlist.config

import me.avo.spotify.dynamic.reddit.playlist.model.Playlist

data class Configuration(
    val userId: String,
    val playlists: List<Playlist>
)