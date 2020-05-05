package me.avo.spottit.service.spotify

import com.wrapper.spotify.model_objects.specification.Track
import io.mockk.every
import io.mockk.mockk
import me.avo.spottit.artist
import me.avo.spottit.makePlaylist
import me.avo.spottit.service.TrackFinderService
import me.avo.spottit.service.reddit.RedditServiceFake
import me.avo.spottit.track
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.size

internal class TrackFinderServiceTest {

    @Test fun `should filter out duplicates by name`() {
        // Given: 2 tracks with different id, but same name / artist
        val artist = "Myst"
        val name = "test"
        val tracks = listOf(
            track("1") {
                setName(name)
                setArtists(artist(artist))
            },
            track("2") {
                setName(name)
                setArtists(artist(artist))
            }
        )
        val trackFinderService = setUpDuplicateTrackScenario(tracks)

        // When
        val tracksFound = trackFinderService.run()

        // Then
        expectThat(tracksFound).size.isEqualTo(1)
    }

    @Test fun `should filter out duplicates by id`() {
        // Given: 2 tracks with same id
        val id = "1"
        val tracks = listOf(track(id), track(id))
        val trackFinderService = setUpDuplicateTrackScenario(tracks)

        // When
        val tracksFound = trackFinderService.run()

        // Then
        expectThat(tracksFound).size.isEqualTo(1)
    }

    private fun setUpDuplicateTrackScenario(tracks: List<Track>): TrackFinderService {
        val spotifyService = mockk<SpotifyService>() {
            every { findTracks(any(), any()) }.returns(tracks)
        }
        val redditService = RedditServiceFake(1)
        return TrackFinderService(makePlaylist(), redditService, mockk(), spotifyService)
    }
}
