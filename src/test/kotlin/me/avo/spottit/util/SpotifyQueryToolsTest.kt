package me.avo.spottit.util

import me.avo.spottit.artist
import me.avo.spottit.redditTrack
import me.avo.spottit.track
import me.avo.spottit.util.SubmissionParser.parse
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import strikt.api.expectThat
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isTrue
import java.net.URL
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
                    setArtists(artist(artist))
                    setName(name)
                    setDurationMs(duration)
                }
            }.toTypedArray()

            val comparator = SpotifyQueryTools.makeComparator(expectedName, expectedArtist)

            val actual = tracks.toList().shuffled(Random(925L)).sortedWith(comparator).first()

            val expected = candidates.single(Candidate::isCorrect)
            expectThat("${actual.name} ${actual.durationMs}").isEqualTo(expected.toString())
        }
    }

    private val threshold = 15 // TODO this should be more generally available

    @Test fun `should filter out candidates exceeding the threshold`() {
        val case = TestCase(
            "Justice", "Cross", listOf(
                Candidate("Justice", "DVNO - Live Version", 400)
            )
        )

        val result = SpotifyQueryTools.sortItems(
            case.candidates.map { it.toTrack() }.toTypedArray(),
            redditTrack(case.artist, case.name),
            threshold
        )

        expectThat(result).isEmpty()
    }

    private val shouldPass = listOf(
        "Sound Rush ft. Michael Jo - Breakaway (Official Videoclip)" to
                Candidate("Sound Rush", "Breakaway (ft. Michael Jo)", 1000)
    )

    @TestFactory fun `should pass`() = shouldPass.map { (raw, candidate) ->
        DynamicTest.dynamicTest(raw) {
            val reddit = parse(raw, null, "", Date())
            val spotify = candidate.toTrack()
            SpotifyQueryTools.getTrackDistance(spotify, reddit)
            val exceedsThreshold = SpotifyQueryTools.exceedsThreshold(spotify, reddit, threshold)
            expectThat(exceedsThreshold).isFalse()
        }
    }

    @Test fun `should not exceed threshold`() {
        val reddit = redditTrack("Aphex Twin", "minipops 67", "source field mix", listOf("120.2"))
        val spotify = track {
            setArtists(artist("Aphex Twin"))
            setName("minipops 67 [120.2][source field mix]")
        }

        SpotifyQueryTools.getTrackDistance(spotify, reddit).also(::println)
        val exceedsThreshold = SpotifyQueryTools.exceedsThreshold(spotify, reddit, threshold)
        expectThat(exceedsThreshold).isFalse()
    }

    @Test fun `bootleg should be more difficult to match`() {
        val redditTrack = redditTrack(
            artist = "Dynoro & Gigi D'Agostino",
            title = "In My Mind",
            extraInformation = listOf("2018", "Adaro & The Machine Bootleg"),
            flair = "Track",
            mix = "Adaro & The Machine Bootleg",
            url = URL("https://m.youtube.com/watch?v=saCXuNkfw1E")
        )
        val spotify = track {
            setArtists(artist("Dynoro"), artist("Gigi D'Agostino"))
            setName("In My Mind")
        }
        SpotifyQueryTools.getTrackDistance(spotify, redditTrack).also(::println)
        val isExceeded = SpotifyQueryTools.exceedsThreshold(spotify, redditTrack, threshold)
        expectThat(isExceeded).isTrue()
    }

    @Test fun `edit should not match original`() {
        val redditTrack = parse("Tiësto - Adagio For Strings (Sub Zero Project Edit)", null, "", Date())
        val spotify = track {
            setArtists(artist("Tiësto"))
            setName("Adagio For Strings")
        }
        SpotifyQueryTools.getTrackDistance(spotify, redditTrack).also(::println)
        val exceedsThreshold = SpotifyQueryTools.exceedsThreshold(spotify, redditTrack, threshold)
        expectThat(exceedsThreshold).isTrue()
    }

    private fun Candidate.toTrack() = track {
        setArtists(artist(artist))
        setName(name)
        setDurationMs(duration)
    }

    @TestFactory fun `fixTitle should produce expected output`() =
        listOf("Get Free (feat. Amber of Dirty Projectors)" to "Get Free").map { (input, expected) ->
            DynamicTest.dynamicTest(input) {
                with(SpotifyQueryTools) {
                    expectThat(input.fixTitle()).isEqualTo(expected)
                }
            }
        }
}
