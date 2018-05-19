package me.avo.spotify.dynamic.reddit.playlist.util

import me.avo.spotify.dynamic.reddit.playlist.config.Configuration
import me.avo.spotify.dynamic.reddit.playlist.model.Playlist
import me.avo.spotify.dynamic.reddit.playlist.model.PostFilter
import net.dean.jraw.models.SubredditSort
import net.dean.jraw.models.TimePeriod
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Test

internal class YamlConfigReaderTest {

    @Test fun `should parse config`() {

        val yaml = this::class.java.classLoader.getResource("config.yml").readText()

        val userId = "110022034"
        val expected = Configuration(
            userId = userId,
            playlists = listOf(
                Playlist(
                    id = "someplid1",
                    maxSize = 20,
                    subreddit = "subred",
                    userId = userId,
                    postFilters = listOf(
                        PostFilter(SubredditSort.TOP, TimePeriod.WEEK, 10)
                    )
                ),
                Playlist(
                    id = "someplid2",
                    maxSize = 30,
                    subreddit = "othersub",
                    userId = userId,
                    postFilters = listOf(
                        PostFilter(SubredditSort.TOP, TimePeriod.ALL, 10)
                    )
                )
            )
        )

        val actual = YamlConfigReader.read(yaml)

        actual shouldEqual expected
    }

}