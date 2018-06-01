package me.avo.spottit.model

import me.avo.spottit.util.SubmissionParser
import java.net.URL

data class RedditTrack(
    val artist: String,
    val title: String,
    val mix: String?,
    val extraInformation: List<String>,
    val flair: String?,
    val url: String
) {

    val isRemix get() = mix?.contains("remix", ignoreCase = true)

    val artists = artist.split("&")

    val firstArtist get() = artists.first()

    val isSpotifyUrl = SubmissionParser.isSpotifyUrl(URL(url))

}