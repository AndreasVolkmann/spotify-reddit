package me.avo.spottit.service.spotify

import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.model.RedditTrack
import me.avo.spottit.model.TrackInPlaylist
import me.avo.spottit.util.SpotifyPlaylistCalculator
import me.avo.spottit.util.TrackFilter
import me.avo.spottit.util.format
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SpotifyServiceImpl(
    private val spotifyApi: SpotifyApiService,
    private val getSearchAlgorithm: (TrackFilter) -> SpotifySearchAlgorithm
) : SpotifyService {

    override fun updatePlaylist(tracks: List<Track>, playlistId: String, maxSize: Int) {
        logger.info("Updating Playlist with ${tracks.size} potential tracks")
        val tracksToAdd = clearPlaylist(tracks, playlistId, maxSize)
        logger.info("Adding ${tracksToAdd.size} tracks to the playlist")
        addTracks(tracksToAdd, playlistId)
    }

    override fun findTracks(tracks: List<RedditTrack>, trackFilter: TrackFilter): List<Track> =
        getSearchAlgorithm(trackFilter).searchForTracks(tracks)

    private fun clearPlaylist(tracksToAdd: List<Track>, playlistId: String, maxSize: Int): List<Track> {
        logger.info("Clearing Playlist")
        val tracksInPlaylist = spotifyApi.getPlaylistsTracks(playlistId)
        val playlistCalculator = SpotifyPlaylistCalculator(tracksInPlaylist)
        return when {
            tracksInPlaylist.isEmpty() -> playlistCalculator.getMaxSizeTracks(tracksToAdd, maxSize)
            else -> {
                val (toRemove, toAdd) = playlistCalculator.calculateTracksToRemoveAndAdd(tracksToAdd, maxSize)
                removeTracksFromPlaylist(playlistId, toRemove)
                toAdd
            }
        }
    }

    private fun removeTracksFromPlaylist(playlistId: String, tracks: List<TrackInPlaylist>) {
        tracks.forEach { logger.info("Removing ${it.track.format()}") }
        spotifyApi.removeTracksFromPlaylist(playlistId, tracks)
    }

    private fun addTracks(tracks: Collection<Track>, playlistId: String) {
        if (tracks.isEmpty()) {
            return logger.warn("Did not find any tracks to add")
        }
        tracks.forEach { logger.info("Adding ${it.format()}") }
        spotifyApi.addTracksToPlaylist(playlistId, tracks)
    }

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
}
