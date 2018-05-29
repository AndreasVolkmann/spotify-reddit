package me.avo.spottit.util

import me.avo.spottit.redditTrack
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

internal class SubmissionParserTest {

    @TestFactory fun parse() = pairs.map { (raw, expected) ->
        DynamicTest.dynamicTest(expected.artist) {
            SubmissionParser.parse(raw, null) shouldEqual expected
        }
    }

    private val pairs = listOf(
        "Arty & Andrew Bayer - Follow The Light" to redditTrack("Arty & Andrew Bayer", "Follow The Light"),
        "Sasha - Wavy Gravy (Paul Van Dyk Remix) [Unreleased]" to redditTrack(
            "Sasha",
            "Wavy Gravy",
            "Paul Van Dyk Remix",
            listOf("Unreleased")
        ),
        "The Dark Pheenix - Lost &amp; Found (Original Extended Mix) [2018]" to redditTrack(
            "The Dark Pheenix",
            "Lost & Found",
            "Original Extended Mix",
            listOf("2018")
        ),
        "Signalrunners & Julie Thompson - These Shoulders (Oliver Smith Remix) [Anjunabeats] (2008)" to redditTrack(
            "Signalrunners & Julie Thompson",
            "These Shoulders",
            "Oliver Smith Remix",
            listOf("Anjunabeats", "2008")
        ),
        "DJ Shadow - Midnight In A Perfect World [Hip Hop/Trip Hop] (1996) way ahead of its time." to redditTrack(
            "DJ Shadow",
            "Midnight In A Perfect World",
            null,
            listOf("Hip Hop/Trip Hop", "1996")
        ),
        "Lorde - Tennis Court [Flume Remix] [future bass]" to redditTrack(
            "Lorde",
            "Tennis Court",
            "Flume Remix",
            listOf("future bass")
        ),
        "Daft Funk - Face To Face [2001]" to redditTrack(
            "Daft Funk",
            "Face To Face",
            null,
            listOf("2001")
        ),
        "Ellie Goulding and Madeon - \"Stay Awake\"" to redditTrack(
            "Ellie Goulding & Madeon",
            "Stay Awake",
            null,
            listOf()
        ),
        "[NEW] Justice - Randy" to redditTrack(
            "Justice",
            "Randy",
            null,
            listOf("NEW")
        ),
        "Flume -- Holdin' On [Flume Step/Electronic] (2013) who else here is a Flume fan?" to redditTrack(
            "Flume",
            "Holdin' On",
            null,
            listOf("Flume Step/Electronic", "2013")
        ),
        "ODESZA - It's Only (feat. Zyra) [Chill] (2014)" to redditTrack(
            "ODESZA",
            "It's Only",
            "feat. Zyra",
            listOf("Chill", "2014")
        ),
        "VIRTUAL SELF - EON BREAK [Electronic] (2017) Porter Robinson's side project?" to redditTrack(
            "VIRTUAL SELF",
            "EON BREAK",
            null,
            listOf("Electronic", "2017")
        ),
        "Major Lazer feat. Amber -- \"Get Free\"" to redditTrack(
            "Major Lazer & Amber",
            "Get Free",
            null,
            listOf()
        )
    )

}