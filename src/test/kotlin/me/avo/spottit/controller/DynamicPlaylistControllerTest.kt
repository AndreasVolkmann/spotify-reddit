package me.avo.spottit.controller

import com.github.salomonbrys.kodein.instance
import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.config.kodein
import me.avo.spottit.model.Configuration
import org.amshove.kluent.shouldBe
import org.junit.jupiter.api.Test

internal class DynamicPlaylistControllerTest {

    @Test fun `checkTrackLength should calculate correctly`() {
        val configuration = Configuration("", listOf(), listOf(), 1)
        val trackAbove = Track.Builder().apply {
            setDurationMs(2000)
        }.build()

        val trackBelow = Track.Builder().apply {
            setDurationMs(999)
        }.build()

        val playlistController: DynamicPlaylistController = kodein.instance()

        playlistController.checkTrackLength(configuration, trackAbove) shouldBe true
        playlistController.checkTrackLength(configuration, trackBelow) shouldBe false
    }

}