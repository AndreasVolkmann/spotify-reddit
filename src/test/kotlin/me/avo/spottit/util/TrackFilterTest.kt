package me.avo.spottit.util

import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.model.Configuration
import org.amshove.kluent.shouldBe
import org.junit.jupiter.api.Test

internal class TrackFilterTest {

    @Test fun `checkTrackLength should calculate correctly`() {
        val configuration = Configuration("", listOf(), listOf(), 1)
        val trackAbove = Track.Builder().apply {
            setDurationMs(2000)
        }.build()

        val trackBelow = Track.Builder().apply {
            setDurationMs(999)
        }.build()

        val trackFilter = TrackFilter(configuration)

        trackFilter.checkTrackLength(trackAbove) shouldBe true
        trackFilter.checkTrackLength(trackBelow) shouldBe false
    }

}