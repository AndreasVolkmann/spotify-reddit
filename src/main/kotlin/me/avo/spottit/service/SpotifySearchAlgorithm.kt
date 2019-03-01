package me.avo.spottit.service

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.exceptions.detailed.BadGatewayException
import com.wrapper.spotify.exceptions.detailed.TooManyRequestsException
import com.wrapper.spotify.model_objects.specification.Paging
import com.wrapper.spotify.model_objects.specification.Track
import com.wrapper.spotify.requests.data.search.simplified.SearchTracksRequest
import me.avo.spottit.model.RedditTrack

interface SpotifySearchAlgorithm {

    val spotifyApi: SpotifyApi

    fun searchForTracks(tracks: List<RedditTrack>): List<Track>

    fun SearchTracksRequest.executeRequest(stack: Int = 0): Paging<Track> = try {
        execute()
    } catch (ex: Exception) {
        val timeout = when (ex) {
            is TooManyRequestsException -> ex.retryAfter
            is BadGatewayException -> 1000
            else -> throw ex
        }
        when {
            stack < 5 -> {
                val waitForSeconds = timeout / 1000L
                Thread.sleep(waitForSeconds)
                executeRequest(stack + 1)
            }
            else -> throw ex
        }
    }
}
