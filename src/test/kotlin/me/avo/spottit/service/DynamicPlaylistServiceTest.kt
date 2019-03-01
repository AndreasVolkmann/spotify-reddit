package me.avo.spottit.service

import me.avo.spottit.TestKodeinAware
import me.avo.spottit.getTestConfig
import org.junit.jupiter.api.Test
import org.kodein.di.generic.instance

internal class DynamicPlaylistServiceTest : TestKodeinAware {

    private val dynamicPlaylistService: DynamicPlaylistService by instance()

    @Test fun update() {
        dynamicPlaylistService.updatePlaylists(getTestConfig())
    }
}
