package me.avo.spottit.service.reddit

import me.avo.spottit.model.DateFilter
import me.avo.spottit.model.Playlist
import me.avo.spottit.model.RedditCredentials
import me.avo.spottit.model.TagFilter
import me.avo.spottit.redditTrack
import net.dean.jraw.models.SubredditSort
import net.dean.jraw.models.TimePeriod
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.size
import java.net.URL

internal class RedditServiceImplTest {

    /**
     * The implementation now relies on URL directly, so invalid specs cannot be supplied.
     */
    @Test fun `processRedditTracks should only accept valid urls`() {
        // Given
        val playlist = Playlist(
            "", 10, "", SubredditSort.TOP, TimePeriod.ALL, 1, true,
            TagFilter(listOf(), listOf(), listOf(), listOf()),
            DateFilter(null, null), true
        )

        val redditService = RedditServiceImpl(playlist, listOf(), 1, RedditCredentials("", "", ""))

        val redditTracks = listOf(
            redditTrack("", "", url = null),
            redditTrack("", "", url = URL("https://open.spotify.com/album/1vWnB0hYmluskQuzxwo25a")),
            redditTrack("", "", url = URL("https://some.stuff"))
        ).asSequence()

        // When
        val result = redditService.processRedditTracks(redditTracks).toList()

        // Then
        expectThat(result).size.isEqualTo(2)
    }
}
