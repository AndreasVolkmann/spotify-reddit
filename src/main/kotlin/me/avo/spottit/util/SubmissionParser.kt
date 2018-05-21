package me.avo.spottit.util

import me.avo.spottit.model.RedditTrack

object SubmissionParser {

    fun parse(title: String, flairText: String?): RedditTrack {
        val fixedTitle = title.fixChars()
        val extraInformation = fixedTitle.getExtraInformation()
        val mix = fixedTitle.getMix()
        val fullTitle = (extraInformation + mix).fold(fixedTitle) { acc, s -> acc.replace(s, "").trim() }
        return RedditTrack(
            artist = fullTitle.substringBefore("-").trim(),
            title = fullTitle.substringAfter("-").trim(),
            mix = mix.firstOrNull()?.removePrefixSuffix(),
            extraInformation = (extraInformation + mix.drop(1)).map { it.removePrefixSuffix() },
            flair = flairText
        )
    }

    private fun String.fixChars() = replace("&amp;", "&")

    private fun String.getMix() = getEnclosedText("(", ")")

    private fun String.getExtraInformation(): List<String> = getEnclosedText("[", "]")

    private fun String.getEnclosedText(start: String, end: String) = Regex("\\$start.*?\\$end")
        .findAll(this)
        .map(MatchResult::value)
        .toList()

    private val prefixSuffixChars = listOf(
        "(" to ")",
        "[" to "]"
    )

    private fun String.removePrefixSuffix(): String =
        prefixSuffixChars.fold(this) { acc, (prefix, suffix) -> acc.removeSurrounding(prefix, suffix) }

}