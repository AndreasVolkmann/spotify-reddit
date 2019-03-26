package me.avo.spottit.util

import com.wrapper.spotify.enums.ReleaseDatePrecision
import com.wrapper.spotify.enums.ReleaseDatePrecision.*
import com.wrapper.spotify.model_objects.specification.Album
import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.TestKodeinAware
import me.avo.spottit.album
import me.avo.spottit.makeConfig
import me.avo.spottit.model.*
import net.dean.jraw.models.SubredditSort
import net.dean.jraw.models.TimePeriod
import org.amshove.kluent.shouldBe
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.TestInstance
import org.kodein.di.generic.factory2

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class TrackFilterTest : TestKodeinAware {

    private val getTrackFilter: (Configuration, Playlist) -> TrackFilter by factory2()

    @Test fun `checkTrackLength should calculate correctly`() {
        val configuration = Configuration(listOf(), listOf(), 1, 500, Schedule(null, null))
        val trackAbove = Track.Builder().apply {
            setDurationMs(2000)
        }.build()

        val trackBelow = Track.Builder().apply {
            setDurationMs(999)
        }.build()

        val trackFilter = getTrackFilter(
            configuration,
            Playlist(
                "", 5, "", SubredditSort.CONTROVERSIAL, TimePeriod.ALL, null, false,
                TagFilter(listOf(), listOf(), listOf(), listOf()), DateFilter(null, null), false
            )
        )

        trackFilter.checkTrackLength(trackAbove) shouldBe true
        trackFilter.checkTrackLength(trackBelow) shouldBe false
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

    private fun TrackFilter.check(pair: Pair<Album, Boolean>) = checkTrackAgeByAlbum(pair.first) shouldBe pair.second

    private fun makeAlbum(date: String, precision: ReleaseDatePrecision, expectation: Boolean): Pair<Album, Boolean> =
        album {
            setReleaseDate(date)
            setReleaseDatePrecision(precision)
        } to expectation

}
