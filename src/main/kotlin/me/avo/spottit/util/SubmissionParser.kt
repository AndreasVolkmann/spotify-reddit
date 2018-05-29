package me.avo.spottit.util

import me.avo.spottit.model.RedditTrack

object SubmissionParser {

    fun parse(title: String, flairText: String?): RedditTrack {
        val extraInformation = title.getExtraInformation()
        //val extraContainsMix = extraInformation.any { it.contains("remix", ignoreCase = true) }
        val mixList = title.getMix()
        val mix = mixList.firstOrNull()?.removePrefixSuffix() ?: extraInformation.find {
            it.contains("remix", ignoreCase = true)
        }?.removePrefixSuffix()

        val date = mix?.toIntOrNull()?.toString()

        val fullTitle = (extraInformation + mixList)
            .fold(title) { acc, s -> acc.replace(s, "").trim() }
            .let { if (it.contains("   ")) it.split("   ").first() else it } // remove additional text after mix / info
            .escapeChars()
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

    private val featuringSynonyms = listOf("ft.", "feat.", "&amp;")

    private val charsToFix = listOf(
        "\"" to "",
        //"'" to "",
        " and " to " & "
    ) + featuringSynonyms.map { it to "&" }

    private fun String.escapeChars() = charsToFix.fold(this) { acc, (old, new) -> acc.replace(old, new) }

    private fun String.getMix() = getEnclosedText("(", ")")

    private fun String.getExtraInformation(): List<String> = getEnclosedText("[", "]")

    private val prefixSuffixChars = listOf(
        "(" to ")",
        "[" to "]",
        "\"" to "\"",
        "'" to "'"
    )

    private fun String.removePrefixSuffix(): String =
        prefixSuffixChars.fold(this) { acc, (prefix, suffix) -> acc.removeSurrounding(prefix, suffix) }

}