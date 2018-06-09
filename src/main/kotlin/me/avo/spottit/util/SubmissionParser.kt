package me.avo.spottit.util

import me.avo.spottit.model.RedditTrack
import me.avo.spottit.model.TagFilter
import java.net.URL
import java.util.*

object SubmissionParser {

    fun parse(title: String, flairText: String?, url: String, created: Date): RedditTrack {
        val (fullTitle, mix, extraInformation) = processTitle(title)
        return RedditTrack(
            artist = fullTitle.substringBefore("-").trim(),
            title = fullTitle.substringAfter("-").cleanTitle(),
            mix = mix?.removePrefixSuffix(),
            extraInformation = (extraInformation - mix)
                .filterNotNull()
                .map { it.removePrefixSuffix() },
            flair = flairText,
            url = url,
            created = created
        )
    }

    fun processTitle(title: String): Triple<String, String?, List<String>> {
        val extraInformation = title.getExtraInformation()
        val mix = extraInformation.find { it.contains("mix", ignoreCase = true) }

        val fullTitle = (extraInformation)
            .fold(title) { acc, s -> acc.replace(s, "").trim() }
            .split("  ").first() // remove additional text after mix / info
            .escapeChars()
        return Triple(fullTitle, mix, extraInformation)
    }

    fun isValidTrackTitle(title: String): Boolean = processTitle(title).first.contains("-")

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

    fun isSpotifyAlbum(url: URL): Boolean = isSpotifyUrl(url) && url.file.startsWith("/album", ignoreCase = true)

    fun isSpotifyTrack(url: URL) = isSpotifyUrl(url) && url.file.startsWith("/track", ignoreCase = true)

    fun isSpotifyUrl(url: URL): Boolean = url.host == "open.spotify.com"

    fun filterTags(track: RedditTrack, tagFilter: TagFilter): Boolean {
        val tags = track.extraInformation
        val (include, includeExact, exclude, excludeExact) = tagFilter
        return includesTag(tags, includeExact, true)
                && includesTag(tags, include, false)
                && excludesTag(tags, excludeExact, true)
                && excludesTag(tags, exclude, false)
    }

    /**
     * Returns true when at least one of [matchTags] is included in [trackTags].
     * Returns false when none of [matchTags] are included in [trackTags].
     * When [isExact] is true, the tags have to match exactly.
     */
    fun includesTag(trackTags: List<String>, matchTags: List<String>, isExact: Boolean): Boolean = when {
        matchTags.isEmpty() -> true
        else -> matchTags.any { match ->
            trackTags.any { tag ->
                when {
                    isExact -> tag == match
                    else -> tag.contains(match, ignoreCase = true)
                }
            }
        }
    }

    /**
     * Returns true when none of [matchTags] are included in [trackTags].
     * Returns false when one or more of [matchTags] are included in [trackTags].
     * When [isExact] is true, the tags have to match exactly.
     */
    fun excludesTag(trackTags: List<String>, matchTags: List<String>, isExact: Boolean): Boolean = when {
        matchTags.isEmpty() -> true
        else -> matchTags.none { match ->
            trackTags.any { tag ->
                when {
                    isExact -> tag == match
                    else -> tag.contains(match, ignoreCase = true)
                }
            }
        }
    }

}