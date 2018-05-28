package me.avo.spottit.util

import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.model.RedditTrack

object SpotifyQueryTools {

    fun initialQuery(track: RedditTrack, useTags: Boolean): String = with(track) {
        when (useTags) {
            true -> makeQuery("artist:$artist", title, mix)
            else -> makeQuery(artist, title, mix)
        }
    }

    fun makeQuery(vararg items: String?): String = items.filterNotNull().joinToString(" ")

    fun sortItems(items: Array<Track>, track: RedditTrack): List<Track> = items.sortedWith(makeComparator(track.title))

    fun makeComparator(title: String): Comparator<Track> =
        compareBy<Track>({ it.name.compareTo(title) != 0 }).thenByDescending { it.durationMs }

}