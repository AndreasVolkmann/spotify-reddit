package me.avo.spottit.util

import com.wrapper.spotify.model_objects.specification.Track
import java.util.*

object SpotifyPlaylistCalculator {

    /**
     * Creates a look up of the index / position of tracks in the playlist.
     * Calling .pop() on the value / list will return the highest index of the track in the playlist
     */
    fun createIndexLookup(tracksInPlaylist: List<Track>) = tracksInPlaylist
        .mapIndexed { index, track -> track.id to index } // map track id to index
        .groupBy { (id) -> id } // group by
        .mapValues { (_, list) ->
            list.map { (_, index) -> index }.sortedDescending() // take indices and sort highest first
        }
        .mapValues { (_, indices) -> LinkedList(indices) } // create a LinkedList so that we can pop from the stack

}