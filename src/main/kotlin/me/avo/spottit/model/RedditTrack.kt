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

    //val artists = artist.split("&")

    val isSpotifyTrack get() = SubmissionParser.isSpotifyTrack(URL(url))

}