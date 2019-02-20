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

    fun sortItems(items: Array<Track>, track: RedditTrack, editDistanceThreshold: Int): List<Track> = items
        .filterNot { exceedsThreshold(it, track, editDistanceThreshold) }
        .sortedWith(makeComparator(track.title, track.artist))

    fun exceedsThreshold(track: Track, redditTrack: RedditTrack, editDistanceThreshold: Int): Boolean {
        val artistDistance = getArtistDistance(track, redditTrack)
        val trackDistance = getTrackDistance(track, redditTrack)
        return artistDistance + trackDistance > editDistanceThreshold
    }

    private fun getArtistDistance(track: Track, artist: String) = track.firstArtistName.getLevenshteinDistance(artist)

    private fun getArtistDistance(track: Track, redditTrack: RedditTrack) = getArtistDistance(track, redditTrack.artist)

    private fun getTrackDistance(track: Track, trackName: String) =
        track.name.fixTitle().getLevenshteinDistance(trackName)

    fun getTrackDistance(track: Track, redditTrack: RedditTrack) =
        getTrackDistance(track, "${redditTrack.title} ${redditTrack.mix}".trim())

    fun makeComparator(title: String, artist: String): Comparator<Track> = compareBy<Track>(
        { getArtistDistance(it, artist) },
        { getTrackDistance(it, title) }
    ).thenByDescending { it.durationMs }

    private fun String.getLevenshteinDistance(other: String): Int =
        LevenshteinDistance.getDefaultInstance().apply(this, other)

    fun String.fixTitle(): String = getEnclosedText("(", ")")
        .filter { it.startsWith("(feat.") }
        .fold(this) { acc, old -> acc.replace(old, "").trim() }
}
