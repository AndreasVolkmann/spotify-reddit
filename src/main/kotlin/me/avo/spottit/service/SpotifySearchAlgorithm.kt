package me.avo.spottit.service

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.exceptions.detailed.TooManyRequestsException
import com.wrapper.spotify.model_objects.specification.Paging
import com.wrapper.spotify.model_objects.specification.Track
import com.wrapper.spotify.requests.data.search.simplified.SearchTracksRequest
import me.avo.spottit.model.RedditTrack

interface SpotifySearchAlgorithm {

    fun searchForTracks(spotifyApi: SpotifyApi, tracks: List<RedditTrack>): List<Track>

    fun SearchTracksRequest.executeRequest(stack: Int = 0): Paging<Track> = try {
        execute()
    } catch (ex: TooManyRequestsException) {
        when {
            stack < 5 -> {
                val waitForSeconds = ex.retryAfter / 1000L
                Thread.sleep(waitForSeconds)
                executeRequest(stack + 1)
            }
            else -> throw ex
        }
    }

}