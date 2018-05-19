package me.avo.spotify.dynamic.reddit.playlist.model

data class Playlist(
    val id: String,
    val userId: String,
    val maxSize: Int,
    val subreddit: String,
    val postFilters: List<PostFilter>
)