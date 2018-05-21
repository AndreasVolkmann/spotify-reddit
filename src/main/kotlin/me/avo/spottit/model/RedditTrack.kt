package me.avo.spottit.model

data class RedditTrack(
    val artist: String,
    val title: String,
    val mix: String?,
    val extraInformation: List<String>,
    val flair: String?
) {

    val isRemix get() = mix?.contains("remix", ignoreCase = true)

}