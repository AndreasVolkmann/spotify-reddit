package me.avo.spottit.data

import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.model.RedditTrack

/**
 * Result of the find track operation.
 * @param redditTrack the reddit track to look for.
 * @param chosenTrack the chosen track.
 * @param totalNumberResults the total number of results found.
 */
data class TrackResult(
    val redditTrack: RedditTrack,
    val chosenTrack: Track?,
    val totalNumberResults: Int,
)