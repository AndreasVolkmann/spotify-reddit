package me.avo.spottit.util

import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.model.RedditTrack
import org.apache.commons.text.similarity.LevenshteinDistance

object SpotifyQueryTools {

    fun initialQuery(track: RedditTrack, useTags: Boolean): String = with(track) {
        when (useTags) {
            true -> makeQuery("artist:\"$artist\"", title, mix)
            else -> makeQuery(artist, title, mix)
        }
    }

    fun makeQuery(vararg items: String?): String = items.filterNotNull().joinToString(" ")
        .also(::println)

    fun sortItems(items: Array<Track>, track: RedditTrack): List<Track> = items
        .sortedWith(makeComparator(track.title, track.artist))

    fun makeComparator(title: String, artist: String): Comparator<Track> =
        compareBy<Track>(
            { it.artistString().getLevenshteinDistance(artist) },
            { it.name.fixTitle().getLevenshteinDistance(title) })
            .thenByDescending { it.durationMs }

    private fun String.getLevenshteinDistance(other: String): Int =
        LevenshteinDistance.getDefaultInstance().apply(this, other)

    fun String.fixTitle(): String = getEnclosedText("(", ")")
        .filter { it.startsWith("(feat.") }
        .fold(this) { acc, old -> acc.replace(old, "").trim() }

}