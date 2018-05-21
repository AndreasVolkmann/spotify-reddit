package me.avo.spottit.util

import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.model.RedditTrack
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

internal class SubmissionParserTest {

    private val pairs = listOf(
        "Arty & Andrew Bayer - Follow The Light" to track("Arty & Andrew Bayer", "Follow The Light"),
        "Sasha - Wavy Gravy (Paul Van Dyk Remix) [Unreleased]" to track(
            "Sasha",
            "Wavy Gravy",
            "Paul Van Dyk Remix",
            listOf("Unreleased")
        ),
        "The Dark Pheenix - Lost &amp; Found (Original Extended Mix) [2018]" to track(
            "The Dark Pheenix",
            "Lost & Found",
            "Original Extended Mix",
            listOf("2018")
        ),
        "Signalrunners & Julie Thompson - These Shoulders (Oliver Smith Remix) [Anjunabeats] (2008)" to track(
            "Signalrunners & Julie Thompson",
            "These Shoulders",
            "Oliver Smith Remix",
            listOf("Anjunabeats", "2008")
        )
    )

    @TestFactory fun parse() = pairs.map { (raw, expected) ->
        DynamicTest.dynamicTest(expected.artist) {
            SubmissionParser.parse(raw, null) shouldEqual expected
        }
    }

    private fun track(
        artist: String,
        title: String,
        mix: String? = null,
        extraInformation: List<String> = listOf(),
        flair: String? = null
    ) = RedditTrack(artist, title, mix, extraInformation, flair)

}