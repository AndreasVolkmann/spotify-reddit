package me.avo.spottit.util

import com.wrapper.spotify.enums.ReleaseDatePrecision
import com.wrapper.spotify.enums.ReleaseDatePrecision.*
import com.wrapper.spotify.model_objects.specification.Album
import me.avo.spottit.TestKodeinAware
import me.avo.spottit.album
import me.avo.spottit.makeConfig
import me.avo.spottit.model.*
import me.avo.spottit.track
import net.dean.jraw.models.SubredditSort
import net.dean.jraw.models.TimePeriod
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.TestInstance
import strikt.api.expect
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class TrackFilterTest : TestKodeinAware {

    @Test fun `checkTrackLength should calculate correctly`() {
        val configuration = Configuration(listOf(), listOf(), 1, 500, Schedule(null, null))
        val trackAbove = track {
            setDurationMs(2000)
        }
        val trackBelow = track {
            setDurationMs(999)
        }

        val trackFilter = TrackFilter(
            configuration,
            Playlist(
                "", 5, "", SubredditSort.CONTROVERSIAL, TimePeriod.ALL, null, false,
                TagFilter(listOf(), listOf(), listOf(), listOf()), DateFilter(null, null), false
            )
        )

        expect {
            that(trackFilter.checkTrackLength(trackAbove)).isTrue()
            that(trackFilter.checkTrackLength(trackBelow)).isFalse()
        }
    }

    @TestFactory fun `Dates before 2012-04-04 should return false`(): List<DynamicTest> {
        val date = parseDateString("2012-04-04")
        val config = makeConfig(
            DateFilter(startingFrom = date, maxDistance = date)
        )

        val trackFilter = TrackFilter(config, config.playlists.first())

        return listOf(
            makeAlbum("2018-01-02", DAY, true),
            makeAlbum("2012-04-03", DAY, false),
            makeAlbum("2012-04", MONTH, false),
            makeAlbum("2012-05", MONTH, true),
            makeAlbum("2012", YEAR, false),
            makeAlbum("2013", YEAR, true)
        ).map { DynamicTest.dynamicTest("${it.first.releaseDate} should return ${it.second}") { trackFilter.check(it) } }
    }

    private fun TrackFilter.check(pair: Pair<Album, Boolean>) =
            expectThat(checkTrackAgeByAlbum(pair.first)) isEqualTo  pair.second

    private fun makeAlbum(date: String, precision: ReleaseDatePrecision, expectation: Boolean): Pair<Album, Boolean> =
        album {
            setReleaseDate(date)
            setReleaseDatePrecision(precision)
        } to expectation

}
