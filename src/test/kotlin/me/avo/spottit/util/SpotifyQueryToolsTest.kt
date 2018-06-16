package me.avo.spottit.util

import com.wrapper.spotify.model_objects.specification.ArtistSimplified
import me.avo.spottit.redditTrack
import me.avo.spottit.track
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import java.util.*

internal class SpotifyQueryToolsTest {

    private data class TestCase(val artist: String, val name: String, val candidates: List<Candidate>)

    private data class Candidate(
        val artist: String,
        val name: String,
        val duration: Int, val
        isCorrect: Boolean = false
    ) {
        override fun toString(): String = "$name $duration"
    }

    private val testCases = listOf(
        TestCase(
            "Simon Patterson", "Latika", listOf(
                Candidate("Simon Patterson", "Latika", 1),
                Candidate("Simon Patterson", "Latika - Original Mix (Edit)", 10),
                Candidate("Simon Patterson", "Latika", 2, true),
                Candidate("Simon Patterson", "Latika - Original Mix (Edit)", 1)
            )
        ),
        TestCase(
            "Signalrunners", "These Shoulders (Oliver Smith Remix)", listOf(
                Candidate("Signalrunners", "These Shoulders - Original Mix", 596640),
                Candidate("Signalrunners", "These Shoulders", 5),
                Candidate("Signalrunners", "These Shoulders (Oliver Smith Remix)", 411133, true),
                Candidate("Signalrunners", "These Shoulders - Club Mix", 10)
            )
        ),
        TestCase(
            "Daft Punk", "Face To Face", listOf(
                Candidate("Daft Punk", "Face To Face", 400, true),
                Candidate("Daft Punk", "Face To Face / Short Circuit", 455),
                Candidate("Daft Punk", "Face To Face (Demon Remix)", 700),
                Candidate("Daft Punk", "Face To Face (Cosmo Vltelli Remix)", 455)
            )
        ),
        TestCase(
            "Flume", "Holdin' On", listOf(
                Candidate("Flume", "Holdin On", 234, true),
                Candidate("Flume", "Holdin On (Hermitude Remix)", 347),
                Candidate("Flume", "Holdin On feat. Freddie Gibbs", 247)
            )
        ),
        TestCase(
            "Major Lazer", "Get Free", listOf(
                Candidate("Major Lazer", "Get Free (feat. Amber of Dirty Projectors)", 450, true),
                Candidate("Major Lazer", "Get Free - Andy C Remix", 520),
                Candidate("Herizen", "Get Free", 318)
            )
        )
    )

    @TestFactory fun `should order as expected`() = testCases.map { (expectedArtist, expectedName, candidates) ->
        DynamicTest.dynamicTest(expectedName) {
            val tracks = candidates.map { (artist, name, duration) ->
                track {
                    setArtists(ArtistSimplified.Builder().apply {
                        setName(artist)
                    }.build())
                    setName(name)
                    setDurationMs(duration)
                }
            }.toTypedArray()

            val comparator = SpotifyQueryTools.makeComparator(expectedName, expectedArtist)

            val actual = tracks.toList().shuffled(Random(925L)).sortedWith(comparator).first()

            val expected = candidates.single(Candidate::isCorrect)
            "${actual.name} ${actual.durationMs}" shouldBeEqualTo expected.toString()
        }
    }

    @Test fun `should filter out candidates exceeding the threshold`() {
        val case = TestCase(
            "Justice", "Cross", listOf(
                Candidate("Justice", "DVNO - Live Version", 400)
            )
        )

        val result = SpotifyQueryTools.sortItems(
            case.candidates.map { it.toTrack() }.toTypedArray(),
            redditTrack(case.artist, case.name),
            10
        )

        result.shouldBeEmpty()
    }

    @Test fun `should not exceed threshold`() {
        val reddit = redditTrack("Aphex Twin", "minipops 67", "source field mix", listOf("120.2"))
        val spotify = track {
            setArtists(ArtistSimplified.Builder().apply {
              setName("Aphex Twin")
            }.build())
            setName("minipops 67 [120.2][source field mix]")
        }


        SpotifyQueryTools.getTrackDistance(spotify, reddit).also(::println)

        SpotifyQueryTools.exceedsThreshold(spotify, reddit, 10) shouldBe false
    }

    private fun Candidate.toTrack() = track {
        setArtists(ArtistSimplified.Builder().apply {
            setName(artist)
        }.build())
        setName(name)
        setDurationMs(duration)
    }

    @TestFactory fun `fixTitle should produce expected output`() =
        listOf("Get Free (feat. Amber of Dirty Projectors)" to "Get Free")
            .map { (input, expected) ->
                DynamicTest.dynamicTest(input) {
                    with(SpotifyQueryTools) {
                        input.fixTitle() shouldEqual expected
                    }
                }
            }

}