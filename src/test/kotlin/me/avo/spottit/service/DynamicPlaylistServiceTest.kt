package me.avo.spottit.service

import me.avo.spottit.TestKodeinAware
import me.avo.spottit.getTestConfig
import me.avo.spottit.util.YamlConfigReader
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable
import org.kodein.di.generic.instance
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisabledIfEnvironmentVariable(named = "DISABLE_NETWORK_TESTS", matches = "1")
internal class DynamicPlaylistServiceTest : TestKodeinAware {

    private val dynamicPlaylistService: DynamicPlaylistService by instance()
    private val testConfig = getTestConfig()

    @Test fun indieheads() {
        dynamicPlaylistService.updatePlaylists(getSinglePlaylist("indieheads"))
    }

    @Test fun hardstyle() {
        dynamicPlaylistService.updatePlaylists(getSinglePlaylist("hardstyle"))
    }

    @Test fun electronicmusic() {
        dynamicPlaylistService.updatePlaylists(getSinglePlaylist("electronicmusic"))
    }

    @Test fun `top all time list`() {
        val yaml = File("resources/configurations/reddit_electronic_music/monthly.yml").readText()
        val config = YamlConfigReader.read(yaml).let {
            val playlist = it.playlists
                .single { p -> p.id == "7KrteOLz4BKmEF5WTzywvu" }
                .also(::println)
                .copy(id = "492vM7yqKZxqV32jb5UWHf", isPrivate = true, maxSize = 10)
            it.copy(playlists = listOf(playlist))
        }
        dynamicPlaylistService.updatePlaylists(config)
    }

    private fun getSinglePlaylist(name: String) =
        testConfig.copy(playlists = listOf(testConfig.playlists.single { it.subreddit == name }))
}
