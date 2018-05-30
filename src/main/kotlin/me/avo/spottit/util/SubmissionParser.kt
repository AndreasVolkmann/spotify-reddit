package me.avo.spottit.util

import me.avo.spottit.model.RedditTrack

object SubmissionParser {

    fun parse(title: String, flairText: String?): RedditTrack {
        val extraInformation = title.getExtraInformation()
        val mix = extraInformation.find { it.contains("mix", ignoreCase = true) }

        val fullTitle = (extraInformation)
            .fold(title) { acc, s -> acc.replace(s, "").trim() }
            .split("  ").first() // remove additional text after mix / info
            .escapeChars()

        return RedditTrack(
            artist = fullTitle.substringBefore("-").trim(),
            title = fullTitle.substringAfter("-").cleanTitle(),
            mix = mix?.removePrefixSuffix(),
            extraInformation = (extraInformation - mix)
                .filterNotNull()
                .map { it.removePrefixSuffix() },
            flair = flairText
        )
    }

    private fun String.cleanTitle() = removePrefix("-").trim()

    private val featuringSynonyms = listOf("ft.", "feat.", "&amp;")

    private val charsToFix = listOf(
        "\"" to "",
        " and " to " & " // only word occurrences of and, otherwise Randy -> R&y
    ) + featuringSynonyms.map { it to "&" }

    private fun String.escapeChars() = charsToFix.fold(this) { acc, (old, new) -> acc.replace(old, new) }

    private fun String.getExtraInformation(): List<String> = getEnclosedText("[", "]") + getEnclosedText("(", ")")

    private val prefixSuffixChars = listOf(
        "(" to ")",
        "[" to "]",
        "\"" to "\"",
        "'" to "'"
    )

    private fun String.removePrefixSuffix(): String =
        prefixSuffixChars.fold(this) { acc, (prefix, suffix) -> acc.removeSurrounding(prefix, suffix) }

}