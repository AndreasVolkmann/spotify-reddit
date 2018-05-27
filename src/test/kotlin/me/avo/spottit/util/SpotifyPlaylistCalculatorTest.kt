package me.avo.spottit.util

import me.avo.spottit.makeTracks
import me.avo.spottit.track
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class SpotifyPlaylistCalculatorTest {

    @Nested
    class CreateIndexLookup {

        @Test fun `should return sorted indices per id`() {
            val tracks = makeTracks(3) + track("0")
            val result = SpotifyPlaylistCalculator.createIndexLookup(tracks)

            result["0"].shouldNotBeNull().let {
                it.pop() shouldEqualTo 3 // last index of track with id 0
                it.pop() shouldEqualTo 0 // next index of track with id 0 after removing popping the previous
            }

        }

    }


}