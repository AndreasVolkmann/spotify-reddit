package me.avo.spottit.util

import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.model.TrackInPlaylist
import org.slf4j.LoggerFactory
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

    fun calculateTracksToRemoveAndAdd(
        tracksToAdd: List<Track>, maxSize: Int, tracksInPlaylist: List<Track>
    ): Pair<List<TrackInPlaylist>, List<Track>> {
        val idsToAdd = tracksToAdd.map { it.id }
        val (tracksToRemove, willBeAddedAgain) = calculateToRemoveAndAddAgain(
            tracksInPlaylist,
            idsToAdd,
            maxSize
        )

        logger.info("${tracksInPlaylist.size} tracks are already in the playlist, ${tracksToRemove.size} tracks will be removed, ${willBeAddedAgain.size} tracks will be added again / kept")
        return tracksToRemove to calculateTracksToAdd(
            tracksInPlaylist.size,
            tracksToRemove.size,
            tracksToAdd,
            willBeAddedAgain,
            maxSize
        )
    }

    fun calculateToRemoveAndAddAgain(
        tracksInPlaylist: List<Track>,
        idsToAdd: List<String>,
        maxSize: Int
    ): Pair<List<TrackInPlaylist>, List<Track>> {
        val (willBeAddedAgain, willBeRemoved) = tracksInPlaylist.partition { idsToAdd.contains(it.id) }

        val duplicatesToRemove = willBeAddedAgain
            .groupBy { it.id }
            .filterValues { it.size > 1 }
            .values
            .flatMap { it.drop(1) }

        val willBeRemovedWithDuplicates = willBeRemoved + duplicatesToRemove

        val (remove, addAgain) = calculateTracksToRemove(
            tracksInPlaylist.size,
            willBeAddedAgain,
            willBeRemovedWithDuplicates,
            maxSize
        )

        val indexLookup = SpotifyPlaylistCalculator.createIndexLookup(tracksInPlaylist)
        val tracksInPlaylistToRemove = remove.map { trackToRemove ->
            TrackInPlaylist(
                track = trackToRemove,
                index = indexLookup[trackToRemove.id]!!.pop()
            )
        }

        return tracksInPlaylistToRemove to addAgain
    }

    fun calculateTracksToRemove(
        currentSize: Int, willBeAddedAgain: List<Track>, willBeRemoved: List<Track>, maxSize: Int
    ): Pair<List<Track>, List<Track>> {
        val sizeAfterRemoval = currentSize - willBeRemoved.size
        val (remove, addAgain) = when {
            sizeAfterRemoval > maxSize -> {
                val additionallyRemove = willBeAddedAgain.takeLast(sizeAfterRemoval - maxSize)
                val willBeAddedAgainFiltered = willBeAddedAgain.take(willBeAddedAgain.size - additionallyRemove.size)
                (willBeRemoved + additionallyRemove) to willBeAddedAgainFiltered
            }
            else -> willBeRemoved to willBeAddedAgain
        }
        return remove to addAgain.distinctBy { it.id }
    }

    fun addMaxSizeTracks(tracksToAdd: List<Track>, maxSize: Int): List<Track> {
        logger.info("The playlist is currently empty, all found tracks will be added")
        return tracksToAdd.take(maxSize)
    }

    fun calculateTracksToAdd(
        amountInPlaylist: Int,
        amountToRemove: Int,
        tracksToAdd: List<Track>,
        willBeAddedAgain: List<Track>,
        maxSize: Int
    ): List<Track> {
        val amountToAdd = maxSize - (amountInPlaylist - amountToRemove) // 2
        return when {
            amountToAdd < 1 -> listOf()
            else -> {
                val againIds = willBeAddedAgain.map { it.id }
                tracksToAdd
                    .filterNot { againIds.contains(it.id) }
                    .take(amountToAdd)
            }
        }
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

}
