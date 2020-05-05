package me.avo.spottit.util

import me.avo.spottit.makeTracks
import me.avo.spottit.track
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.expect
import strikt.api.expectThat
import strikt.assertions.*

internal class SpotifyPlaylistCalculatorTest {

    private val playlistCalculator = SpotifyPlaylistCalculator(listOf())

    @Nested inner class CreateIndexLookup {

        @Test fun `should return sorted indices per id`() {
            val tracks = makeTracks(3) + track("0")
            val result = SpotifyPlaylistCalculator(tracks).createIndexLookup()

            expectThat(result["0"]).isNotNull().and {
                get { pop() }.isEqualTo(3) // last index of track with id 0
                get { pop() }.isEqualTo(0) // next index of track with id 0 after removing popping the previous
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

            expect {
                that(remove).isEmpty()
                that(add).contains(tracksToAdd.takeLast(5))
            }
        }

        @Test fun `should remove duplicates`() {
            val maxSize = 10
            val tracksToAdd = makeTracks(5)
            val duplicates = makeTracks(2)
            val tracksInPlaylist = makeTracks(5) + duplicates

            val (remove, add) = SpotifyPlaylistCalculator(tracksInPlaylist)
                .calculateTracksToRemoveAndAdd(tracksToAdd, maxSize)

            expect {
                that(remove).size.isEqualTo(2)
                that(add).isEmpty()
            }
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

            expect {
                that(actual).size.isEqualTo(maxSize - tracksInPlaylist.size)
                that(actual.map { it.id }).contains(listOf("2", "3"))
            }
        }

        @Test fun `should return empty list when amountInPlaylist is biggerEqual than maxSize`() {
            val tracksToAdd = playlistCalculator.calculateTracksToAdd(10, 0, listOf(track { }), listOf(), 5)
            expectThat(tracksToAdd).isEmpty()
        }

        @Test fun `should account for removed tracks`() {
            val result = playlistCalculator.calculateTracksToAdd(5, 2, makeTracks(2), listOf(track("0")), 5)
            expectThat(result) {
                size.isEqualTo(1)
                get { first().id }.isEqualTo("1")
            }
        }
    }

    @Nested inner class GetMaxSizeTracks {

        @Test fun `should never return more than maxSize`() {
            val maxSize = 20
            val tracksToAdd = makeTracks(40)
            val result = playlistCalculator.getMaxSizeTracks(tracksToAdd, maxSize)
            expectThat(result).size.isLessThanOrEqualTo(maxSize)
        }

        @Test fun `should return tracksToAdd if there are less than maxSize`() {
            val maxSize = 20
            val tracksToAdd = makeTracks(10)
            val result = playlistCalculator.getMaxSizeTracks(tracksToAdd, maxSize)
            expectThat(result).size.isEqualTo(tracksToAdd.size)
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

            expect {
                that(tracksToRemove).size.isEqualTo(willBeRemoved.size)
                that(tracksToRemove).contains(willBeRemoved)
                that(filteredAddedAgain).isEqualTo(willBeAddedAgain)
            }
        }

        @Test fun `when sizeAfterRemoval is greater than maxSize`() {
            val currentSize = 20
            val willBeAddedAgain = makeTracks(5)
            val willBeRemoved = makeTracks(1, 5)
            val maxSize = 15

            val (tracksToRemove, filteredAddedAgain) = playlistCalculator.calculateTracksToRemove(
                    currentSize,
                    willBeAddedAgain,
                    willBeRemoved,
                    maxSize
            )

            expect {
                that(tracksToRemove) {
                    size.isEqualTo(currentSize - maxSize)
                    contains(willBeRemoved)
                }
                that(filteredAddedAgain) {
                    size.isEqualTo(1)
                    isEqualTo(willBeAddedAgain.take(1))
                }
            }
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

            expect {
                that(tracksToRemove) {
                    size.isEqualTo(5)
                    get { map { it.track.id } }.contains(duplicateId)
                }
                that(willBeAddedAgain) {
                    size.isEqualTo(1)
                    get { first().id }.isEqualTo(duplicateId)
                }
            }
        }
    }
}
