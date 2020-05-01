package me.avo.spottit.model

import me.avo.spottit.util.SubmissionParser
import java.net.URL
import java.util.*

data class RedditTrack(
    val artist: String,
    val title: String,
    val mix: String?,
    val extraInformation: List<String>,
    val flair: String?,
    val url: URL?,
    val created: Date
) {

    val isSpotifyTrack get() = url != null && SubmissionParser.isSpotifyTrack(url)

    val isSpotifyAlbum get() = url != null && SubmissionParser.isSpotifyAlbum(url)

}
