package me.avo.spottit.util

import me.avo.spottit.model.RedditTrack

object SubmissionParser {

    fun parse(title: String, flairText: String?): RedditTrack {
        val fixedTitle = title.escapeChars()
        val extraInformation = fixedTitle.getExtraInformation()
        //val extraContainsMix = extraInformation.any { it.contains("remix", ignoreCase = true) }
        val mixList = fixedTitle.getMix()
        val mix = mixList.firstOrNull()?.removePrefixSuffix() ?: extraInformation.find {
            it.contains("remix", ignoreCase = true)
        }?.removePrefixSuffix()

        val date = mix?.toIntOrNull()?.toString()

        val fullTitle = (extraInformation + mixList)
            .fold(fixedTitle) { acc, s -> acc.replace(s, "").trim() }
            .let { if (it.contains("   ")) it.split("   ").first() else it } // remove additional text after mix / info
        return RedditTrack(
            artist = fullTitle.substringBefore("-").trim(),
            title = fullTitle.substringAfter("-").cleanTitle(),
            mix = if (date != null) null else mix,
            extraInformation = (extraInformation + date + mixList.drop(1))
                .filterNotNull()
                .map { it.removePrefixSuffix() }
                .filter { it != mix || it == date }, // filter mix out of extra info allow if date
            flair = flairText
        )
    }

    private fun String.cleanTitle() = removePrefix("-").trim()

    private val charsToFix = listOf(
        "&amp;" to "&",
        "\"" to "",
        "'" to ""
    )

    private fun String.escapeChars() = charsToFix.fold(this) { acc, (old, new) -> acc.replace(old, new) }

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