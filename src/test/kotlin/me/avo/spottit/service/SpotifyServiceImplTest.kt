package me.avo.spottit.service

import com.github.salomonbrys.kodein.instance
import me.avo.spottit.config.kodein
import me.avo.spottit.track
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldEqualTo
import org.junit.jupiter.api.Test

internal class SpotifyServiceImplTest {

    private val spotifyService = SpotifyServiceImpl(kodein.instance())

    @Test fun `calculateTracksToAdd should add no more than max`() {

        val tracksInPlaylist = listOf(
            track { setId("1") }
        )

        val tracksToAdd = listOf(
            track { setId("1") }, // will be removed
            track { setId("2") },
            track { setId("3") },
            track { setId("4") }
        )

        val willBeAddedAgain = listOf(
            track { setId("1") }
        )

        val maxSize = 3

        val actual = spotifyService.calculateTracksToAdd(tracksInPlaylist.size, tracksToAdd, willBeAddedAgain, maxSize)

        actual.size shouldEqualTo maxSize - tracksInPlaylist.size

        actual.map { it.id } shouldContainAll listOf("2", "3")
    }

    @Test fun `calculateTracksToAdd should return empty list when amountInPlaylist is biggerEqual than maxSize`() {
        spotifyService.calculateTracksToAdd(10, listOf(track { }), listOf(), 5).shouldBeEmpty()
    }

}