package me.avo.spottit.util

import com.wrapper.spotify.model_objects.specification.Track
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.util.*

internal class SpotifyQueryToolsTest {

    private data class TestCase(val expectedName: String, val candidates: List<Candidate>)
    private data class Candidate(val name: String, val duration: Int, val isCorrect: Boolean = false) {
        override fun toString(): String = "$name $duration"
    }

    private val testCases = listOf(
        TestCase(
            "Latika", listOf(
                Candidate("Latika", 1),
                Candidate("Latika - Original Mix (Edit)", 10),
                Candidate("Latika", 2, true),
                Candidate("Latika - Original Mix (Edit)", 1)
            )
        ),
        TestCase(
            "These Shoulders (Oliver Smith Remix)", listOf(
                Candidate("These Shoulders - Original Mix", 596640),
                Candidate("These Shoulders", 5),
                Candidate("These Shoulders (Oliver Smith Remix)", 411133, true),
                Candidate("These Shoulders - Club Mix", 10)
            )
        ),
        TestCase(
            "Face To Face", listOf(
                Candidate("Face To Face", 400, true),
                Candidate("Face To Face / Short Circuit", 455),
                Candidate("Face To Face (Demon Remix)", 700),
                Candidate("Face To Face (Cosmo Vltelli Remix)", 455)
            )
        )
    )

    @TestFactory fun `should order as expected`() = testCases.map { (expectedName, candidates) ->
        DynamicTest.dynamicTest(expectedName) {
            val tracks = candidates.map { (name, duration) ->
                Track.Builder().apply {
                    setName(name)
                    setDurationMs(duration)
                }.build()
            }.toTypedArray()

            val comparator = SpotifyQueryTools.makeComparator(expectedName)

            val actual = tracks.toList().shuffled(Random(925L)).sortedWith(comparator).first()

            val expected = candidates.single(Candidate::isCorrect)
            "${actual.name} ${actual.durationMs}" shouldBeEqualTo expected.toString()
        }
    }

}