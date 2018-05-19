package me.avo.spotify.dynamic.reddit.playlist.model

data class RedditTrack(
    val artist: String,
    val title: String,
    val mix: String?,
    val extraInformation: List<String>,
    val flair: String?
)