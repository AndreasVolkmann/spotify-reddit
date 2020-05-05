package me.avo.spottit.util

import me.avo.spottit.redditTrack
import me.avo.spottit.tagFilter
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import strikt.api.expect
import strikt.api.expectThat
import strikt.assertions.*
import java.net.URL
import java.util.*

internal class SubmissionParserTest {

    @Test fun `invalid url should be parsed as null`() {
        val invalidUrl = "/r/electronicmusic/comments/abc/some/"
        val track = SubmissionParser.parse("Test", null, invalidUrl, Date())
        expectThat(track).get { url }.isNull()
    }

    @Test fun `should identify spotify urls`() {
        val spotifyUrl = URL("https://open.spotify.com/album/1vWnB0hYmluskQuzxwo25a")
        val otherUrl = URL("https://github.com/AndreasVolkmann/spotify-reddit/issues/19")

        expect {
            that(SubmissionParser.isSpotifyUrl(spotifyUrl)).isTrue()
            that(SubmissionParser.isSpotifyUrl(otherUrl)).isFalse()
        }
    }

    @Test fun `should identify spotify albums`() {
        val albumUrl = URL("https://open.spotify.com/album/1vWnB0hYmluskQuzxwo25a")
        val trackUrl = URL("https://open.spotify.com/track/2t5ePzdyeR5jG1nW65zxce?si=rQspA45BRM63pqAVOJ9FDA")

        expect {
            that(SubmissionParser.isSpotifyAlbum(albumUrl)).isTrue()
            that(SubmissionParser.isSpotifyAlbum(trackUrl)).isFalse()
        }
    }

    @Test fun `should identify spotify tracks`() {
        val albumUrl = URL("https://open.spotify.com/album/1vWnB0hYmluskQuzxwo25a")
        val trackUrl = URL("https://open.spotify.com/track/2t5ePzdyeR5jG1nW65zxce?si=rQspA45BRM63pqAVOJ9FDA")

        expect {
            that(SubmissionParser.isSpotifyTrack(albumUrl)).isFalse()
            that(SubmissionParser.isSpotifyTrack(trackUrl)).isTrue()
        }
    }

    @Test fun `should filter out titles without - in actual title`() {
        val invalid = "Dancing to Justice (r/Gifsound x-post)"
        val valid = pairs.map { it.first }

        expect {
            that(SubmissionParser.isValidTrackTitle(invalid)).isFalse()
            that(valid).all { get { SubmissionParser.isValidTrackTitle(this) }.isTrue() }
        }
    }

    @Nested inner class HasValidTag {

        private val trackTags = listOf("test news", "one track")

        private fun includesTag(isExact: Boolean, vararg tags: String) =
                SubmissionParser.includesTag(trackTags, tags.toList(), isExact)

        private fun excludesTag(isExact: Boolean, tag: String) =
                SubmissionParser.excludesTag(trackTags, listOf(tag), isExact)

        @Test fun `should include`() {
            expect {
                that(includesTag(false, "one")).isTrue()
                that(includesTag(false, "two")).isFalse()
            }
        }

        @Test fun `should include exact`() {
            expect {
                that(includesTag(true, "one track")).isTrue()
                that(includesTag(true, "test")).isFalse()
            }
        }

        @Test fun `should exclude`() {
            expect {
                that(excludesTag(false, "one")).isFalse()
                that(excludesTag(false, "two")).isTrue()
            }
        }

        @Test fun `should exclude exact`() {
            expect {
                that(excludesTag(true, "one track")).isFalse()
                that(excludesTag(true, "test")).isTrue()
            }
        }
    }

    @Nested inner class FilterTags {

        @Test fun `include`() {
            val result = SubmissionParser.filterTags(
                redditTrack("", "", null, listOf("test news", "one track")),
                tagFilter(include = listOf("one"))
            )
            expectThat(result).isTrue()
        }

        @Test fun `exclude`() {
            val result = SubmissionParser.filterTags(
                redditTrack("", "", null, listOf("test news", "one track")),
                tagFilter(exclude = listOf("one track"))
            )
            expectThat(result).isFalse()
        }
    }


    @TestFactory fun parse() = pairs.map { (raw, expected) ->
        val staticDate = Date()
        DynamicTest.dynamicTest(expected.artist) {
            val parsed = SubmissionParser.parse(raw, null, "", staticDate)
            expectThat(parsed).isEqualTo(expected.copy(created = staticDate))
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
            null,
            listOf("Chill", "feat. Zyra", "2014")
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
        ),
        "Mat Zo & Porter Robinson - Easy [Progressive House] (2013)" to redditTrack(
            "Mat Zo & Porter Robinson",
            "Easy",
            null,
            listOf("Progressive House", "2013")
        ),
        "The Weeknd - I Feel It Coming(feat. Daft Punk) (2016)" to redditTrack(
            "The Weeknd",
            "I Feel It Coming",
            null,
            listOf("feat. Daft Punk", "2016")
        ),
        "Justice - Stress (Electro House)(2008) How I imagine a good number of people feel right now" to redditTrack(
            "Justice",
            "Stress",
            null,
            listOf("Electro House", "2008")
        ),
        "Boards of Canada -- Music Has the Right to Children (April 20, 1998) Happy 19th Birthday" to redditTrack(
            "Boards of Canada",
            "Music Has the Right to Children",
            null,
            listOf("April 20, 1998")
        ),
        "Aphex Twin - minipops 67 [120.2][source field mix]" to redditTrack(
            "Aphex Twin",
            "minipops 67",
            "source field mix",
            listOf("120.2")
        ),
        "Journeyman— just a man (such great momentum!!)" to redditTrack(
            "Journeyman",
            "just a man",
            null,
            listOf("such great momentum!!")
        )
    )

}
