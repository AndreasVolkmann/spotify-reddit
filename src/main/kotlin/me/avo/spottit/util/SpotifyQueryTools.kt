package me.avo.spottit.util

import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.model.RedditTrack

object SpotifyQueryTools {

    fun initialQuery(track: RedditTrack) = makeQuery(track.artist, track.title, track.mix)

    fun makeQuery(vararg items: String?) = items.filterNotNull().joinToString(" ")

    fun sortItems(items: Array<Track>, track: RedditTrack) = items.sortedWith(makeComparator(track.title))

    fun makeComparator(title: String) =
        compareBy<Track>({ it.name.compareTo(title) != 0 }).thenByDescending { it.durationMs }

}