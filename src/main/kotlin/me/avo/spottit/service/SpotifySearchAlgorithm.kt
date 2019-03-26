package me.avo.spottit.service

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.exceptions.detailed.BadGatewayException
import com.wrapper.spotify.exceptions.detailed.TooManyRequestsException
import com.wrapper.spotify.model_objects.specification.Paging
import com.wrapper.spotify.model_objects.specification.Track
import com.wrapper.spotify.requests.data.search.simplified.SearchTracksRequest
import me.avo.spottit.model.RedditTrack
import me.avo.spottit.util.RetrySupport

interface SpotifySearchAlgorithm : RetrySupport {

    val spotifyApi: SpotifyApi

    fun searchForTracks(tracks: List<RedditTrack>): List<Track>

    override val stackSize get() = 5

    override fun mapTimeout(ex: Exception): Int = when (ex) {
        is TooManyRequestsException -> ex.retryAfter
        is BadGatewayException -> 1000
        else -> throw ex
    }

    fun SearchTracksRequest.executeRequest(stack: Int = 0): Paging<Track> = retry(::execute)

}
