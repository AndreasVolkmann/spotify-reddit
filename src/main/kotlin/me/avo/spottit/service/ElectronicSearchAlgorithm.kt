package me.avo.spottit.service

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.model_objects.specification.Album
import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.model.RedditTrack
import me.avo.spottit.util.SpotifyQueryTools
import me.avo.spottit.util.TrackFilter
import org.slf4j.LoggerFactory

class ElectronicSearchAlgorithm(
    override val spotifyApi: SpotifyApi,
    private val trackFilter: TrackFilter
) : SpotifySearchAlgorithm {

    override fun searchForTracks(tracks: List<RedditTrack>): List<Track> = tracks
        .asSequence()
        .map(::findTrack)
        .mapNotNull { (reddit, chosenTrack, total) ->
            logger.info("Found $total results for: $reddit | ${chosenTrack?.artists?.joinToString { it.name }} - ${chosenTrack?.name}")
            chosenTrack
        }
        .onEach { trackFilter.timeout() }
        .toList()

    fun getAlbumForTrack(track: Track): Album = spotifyApi.getAlbum(track.album.id).build().execute()

    private fun findTrack(track: RedditTrack): Triple<RedditTrack, Track?, Int> = when {
        track.isSpotifyTrack -> getTrack(track)
        else -> searchForTrack(track)
    }

    private fun getTrack(track: RedditTrack): Triple<RedditTrack, Track, Int> {
        val id = track.url.substringAfter("/track/").substringBefore("?")
        val spotifyTrack = spotifyApi.getTrack(id).build().execute()
        return Triple(track, spotifyTrack, if (spotifyTrack != null) 1 else 0)
    }

    private fun searchForTrack(track: RedditTrack): Triple<RedditTrack, Track?, Int> =
        SpotifyQueryTools.initialQuery(track, useTags = true)
            .let(::searchQuery)
            .let { (total, items) ->
                val validTracks = trackFilter.applyFilters(items)
                val chosenTrack = evaluateResults(track, validTracks, 0)?.let {
                    if (trackFilter.doCheckReleaseDate) {
                        val album = getAlbumForTrack(it)
                        if (trackFilter.checkTrackAgeByAlbum(album)) it else null
                    } else it
                }
                Triple(track, chosenTrack, total)
            }

    private fun searchQuery(query: String): Pair<Int, Array<Track>> {
        logger.debug("Searching for $query")
        val paging = spotifyApi.searchTracks(query).limit(10).offset(0).build().executeRequest()
        Thread.sleep(100)
        return paging.total to trackFilter.applyFilters(paging.items)
    }

    private fun evaluateResults(track: RedditTrack, items: Array<Track>, stack: Int): Track? = when (items.size) {
        0 -> adjustQuery(track, stack) // couldn't find anything, adjust query
        1 -> items.first() // only one result
        else -> SpotifyQueryTools.sortItems(items, track, trackFilter.editDistanceThreshold).firstOrNull()
                ?: evaluateResults(track, emptyArray(), stack + 1) // there are multiple results, sort
    }

    private fun adjustQuery(track: RedditTrack, stack: Int) = when {
        stack > 1 -> null
        track.artist.contains("&") -> alteredArtistSearch("&", track, stack)
        track.mix != null && !trackFilter.isStrictMix -> research(track, stack, track.artist, track.title)
        stack == 0 -> research(track, stack, track.artist, track.title, track.mix)
        else -> null
    }

    private fun alteredArtistSearch(delimiter: String, track: RedditTrack, stack: Int): Track? {
        val fixedArtist = track.artist.split(delimiter).first().trim()
        return research(track.copy(artist = fixedArtist), stack, fixedArtist, track.title, track.mix)
    }

    private fun research(track: RedditTrack, stack: Int, vararg items: String?) =
        searchQuery(SpotifyQueryTools.makeQuery(*items)).let { (_, items) ->
            evaluateResults(track, items, stack + 1)
        }

    private val logger = LoggerFactory.getLogger(this::class.java)
}