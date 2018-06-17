package me.avo.spottit.util

import me.avo.spottit.model.*
import net.dean.jraw.models.SubredditSort
import net.dean.jraw.models.TimePeriod
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Test
import java.util.*

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
                    sort = SubredditSort.TOP,
                    timePeriod = TimePeriod.WEEK,
                    minimumUpvotes = 5,
                    isStrictMix = false,
                    tagFilter = TagFilter(
                        listOf(),
                        listOf(),
                        listOf(),
                        listOf()
                    ),
                    dateFilter = DateFilter(
                        startingFrom = parseDateString("2018-02-03"),
                        maxDistance = null
                    ),
                    isPrivate = true
                ),
                Playlist(
                    id = "someplid2",
                    maxSize = 30,
                    subreddit = "othersub",
                    userId = userId,
                    sort = SubredditSort.TOP,
                    timePeriod = TimePeriod.ALL,
                    minimumUpvotes = 10,
                    isStrictMix = true,
                    tagFilter = TagFilter(
                        includeExact = listOf("Edit", "Radio"),
                        include = listOf("FRESH"),
                        excludeExact = listOf("Album"),
                        exclude = listOf("video")
                    ),
                    dateFilter = DateFilter(
                        startingFrom = null,
                        maxDistance = Calendar.getInstance().apply {
                            add(Calendar.YEAR, -2)
                            add(Calendar.MONTH, -3)
                            set(Calendar.HOUR, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }.time
                    ),
                    isPrivate = false
                )
            ),
            flairsToExclude = listOf("one", "Discussion"),
            minimumLength = 100,
            rateLimitInMs = 1000,
            schedule = Schedule(
                dayOfWeek = 2,
                dayOfMonth = 1
            )
        )

        val actual = YamlConfigReader.read(yaml)

        actual shouldEqual expected
    }

    @Test fun `parseMaxDistance should produce correct date`() {
        val year = 2
        val month = 4
        val expected = Calendar.getInstance().apply {
            add(Calendar.YEAR, -year)
            add(Calendar.MONTH, -month)
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
        val actual = YamlConfigReader.parseMaxDistance(month, year)

        actual shouldEqual expected
    }

}