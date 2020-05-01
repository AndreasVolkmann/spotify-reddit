package me.avo.spottit.util

import me.avo.spottit.makeTracks
import me.avo.spottit.track
import org.amshove.kluent.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class SpotifyPlaylistCalculatorTest {

    private val playlistCalculator = SpotifyPlaylistCalculator(listOf())

    @Nested inner class CreateIndexLookup {

        @Test fun `should return sorted indices per id`() {
            val tracks = makeTracks(3) + track("0")
            val result = SpotifyPlaylistCalculator(tracks).createIndexLookup()

            result["0"].shouldNotBeNull().let {
                it.pop() shouldBeEqualTo 3 // last index of track with id 0
                it.pop() shouldBeEqualTo 0 // next index of track with id 0 after removing popping the previous
            }
        }
    }

    @Nested inner class CalculateTracksToRemoveAndAdd {

        @Test fun `should add tracks that are not in playlist yet`() {
            val maxSize = 10
            val tracksToAdd = makeTracks(10)
            val tracksInPlaylist = makeTracks(5)
            val (remove, add) = SpotifyPlaylistCalculator(tracksInPlaylist)
                .calculateTracksToRemoveAndAdd(tracksToAdd, maxSize)

            remove.shouldBeEmpty()
            add.shouldContainAll(tracksToAdd.takeLast(5))
        }

        @Test fun `should remove duplicates`() {
            val maxSize = 10
            val tracksToAdd = makeTracks(5)
            val duplicates = makeTracks(2)
            val tracksInPlaylist = makeTracks(5) + duplicates

            val (remove, add) = SpotifyPlaylistCalculator(tracksInPlaylist)
                .calculateTracksToRemoveAndAdd(tracksToAdd, maxSize)

            remove.size shouldBeEqualTo 2
            add.shouldBeEmpty()
        }
    }

    @Nested inner class CalculateTracksToAdd {

        @Test fun `should add no more than max`() {
            val tracksInPlaylist = listOf(track("1"))

            val tracksToAdd = listOf(
                track { setId("1") }, // will be removed
                track { setId("2") },
                track { setId("3") },
                track { setId("4") }
            )

            val willBeAddedAgain = listOf(track("1"))

            val maxSize = 3

            val actual = playlistCalculator.calculateTracksToAdd(
                tracksInPlaylist.size,
                0,
                tracksToAdd,
                willBeAddedAgain,
                maxSize
            )

            actual.size shouldBeEqualTo maxSize - tracksInPlaylist.size

            actual.map { it.id } shouldContainAll listOf("2", "3")
        }

        @Test fun `should return empty list when amountInPlaylist is biggerEqual than maxSize`() {
            playlistCalculator.calculateTracksToAdd(10, 0, listOf(track { }), listOf(), 5).shouldBeEmpty()
        }

        @Test fun `should account for removed tracks`() {
            val result = playlistCalculator.calculateTracksToAdd(5, 2, makeTracks(2), listOf(track("0")), 5)
            result.size shouldBeEqualTo 1
            result.first().id shouldBeEqualTo "1"
        }
    }

    @Nested inner class GetMaxSizeTracks {

        @Test fun `should never return more than maxSize`() {
            val maxSize = 20
            val tracksToAdd = makeTracks(40)
            val result = playlistCalculator.getMaxSizeTracks(tracksToAdd, maxSize)
            result.size shouldBeLessOrEqualTo maxSize
        }

        @Test fun `should return tracksToAdd if there are less than maxSize`() {
            val maxSize = 20
            val tracksToAdd = makeTracks(10)
            val result = playlistCalculator.getMaxSizeTracks(tracksToAdd, maxSize)
            result.size shouldBeEqualTo tracksToAdd.size
        }
    }

    @Nested inner class CalculateTracksToRemove {

        @Test fun `when sizeAfterRemoval is less than maxSize`() {
            val currentSize = 20
            val willBeAddedAgain = makeTracks(5)
            val willBeRemoved = makeTracks(6, 5)
            val maxSize = 25

            val (tracksToRemove, filteredAddedAgain) =
                playlistCalculator.calculateTracksToRemove(
                    currentSize,
                    willBeAddedAgain,
                    willBeRemoved,
                    maxSize
                )

            tracksToRemove.size shouldBeEqualTo willBeRemoved.size
            tracksToRemove shouldContainAll willBeRemoved
            filteredAddedAgain shouldBeEqualTo willBeAddedAgain
        }

        @Test fun `when sizeAfterRemoval is greater than maxSize`() {
            val currentSize = 20
            val willBeAddedAgain = makeTracks(5)
            val willBeRemoved = makeTracks(1, 5)
            val maxSize = 15

            val (tracksToRemove, filteredAddedAgain) =
                playlistCalculator.calculateTracksToRemove(
                    currentSize,
                    willBeAddedAgain,
                    willBeRemoved,
                    maxSize
                )

            tracksToRemove.size shouldBeEqualTo currentSize - maxSize
            tracksToRemove shouldContainAll willBeRemoved
            tracksToRemove shouldContainSome willBeAddedAgain
            filteredAddedAgain.size shouldBeEqualTo 1
            filteredAddedAgain shouldBeEqualTo willBeAddedAgain.take(1)
        }
    }

    @Nested inner class CalculateToRemoveAndAddAgain {

        @Test fun `should remove duplicates`() {
            val duplicateId = "1"
            val tracksInPlaylist = makeTracks(5) + track { setId(duplicateId) }
            val idsToAdd = listOf(duplicateId)
            val maxSize = 5

            val (tracksToRemove, willBeAddedAgain) = SpotifyPlaylistCalculator(tracksInPlaylist)
                .calculateToRemoveAndAddAgain(idsToAdd, maxSize)

            tracksToRemove.size shouldBeEqualTo 5
            tracksToRemove.map { it.track.id } shouldContain duplicateId

            willBeAddedAgain.map { it.id }
            willBeAddedAgain.first().id shouldBeEqualTo duplicateId
            willBeAddedAgain.size shouldBeEqualTo 1
        }
    }
}
