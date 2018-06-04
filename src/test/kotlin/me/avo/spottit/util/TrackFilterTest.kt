package me.avo.spottit.util

import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.model.Configuration
import me.avo.spottit.model.Playlist
import me.avo.spottit.model.TagFilter
import net.dean.jraw.models.SubredditSort
import net.dean.jraw.models.TimePeriod
import org.amshove.kluent.shouldBe
import org.junit.jupiter.api.Test

internal class TrackFilterTest {

    @Test fun `checkTrackLength should calculate correctly`() {
        val configuration = Configuration("", listOf(), listOf(), 1, 500)
        val trackAbove = Track.Builder().apply {
            setDurationMs(2000)
        }.build()

        val trackBelow = Track.Builder().apply {
            setDurationMs(999)
        }.build()

        val trackFilter = TrackFilter(
            configuration,
            Playlist(
                "", "", 5, "", SubredditSort.CONTROVERSIAL, TimePeriod.ALL, null, false,
                TagFilter(listOf(), listOf(), listOf(), listOf()), false
            )
        )

        trackFilter.checkTrackLength(trackAbove) shouldBe true
        trackFilter.checkTrackLength(trackBelow) shouldBe false
    }

}