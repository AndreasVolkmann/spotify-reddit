package me.avo.spottit.util

import com.apurebase.arkenv.parse
import me.avo.spottit.config.Arguments
import me.avo.spottit.model.*
import net.dean.jraw.models.SubredditSort
import net.dean.jraw.models.TimePeriod
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotContain
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.util.Calendar.*

internal class YamlConfigReaderTest {

    @Test fun `should parse config`() {
        val yaml = readYaml()
        val expected = Configuration(
            playlists = listOf(
                Playlist(
                    id = "someplid1",
                    maxSize = 20,
                    subreddit = "subred",
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
                        maxDistance = getInstance().apply {
                            add(YEAR, -2)
                            add(MONTH, -3)
                            set(HOUR, 0)
                            set(MINUTE, 0)
                            set(SECOND, 0)
                            set(MILLISECOND, 0)
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

        expectThat(actual).isEqualTo(expected)
        actual shouldBeEqualTo expected
    }

    @Test fun `parseMaxDistance should produce correct date`() {
        val year = 2
        val month = 4
        val expected = getInstance().apply {
            add(YEAR, -year)
            add(MONTH, -month)
            set(HOUR, 0)
            set(MINUTE, 0)
            set(SECOND, 0)
            set(MILLISECOND, 0)
        }.time
        val actual = YamlConfigReader.parseMaxDistance(month, year)

        actual shouldBeEqualTo expected
    }

    @Test fun `should only include playlists in useLists when defined`() {
        Arguments.parse(arrayOf("-c", "", "--use-lists", "someplid2"))
        val config = YamlConfigReader.read(readYaml())
        val ids = config.playlists.map(Playlist::id)
        ids shouldContain "someplid2"
        ids shouldNotContain "someplid1"
        Arguments.parse(arrayOf("-c", ""))
    }

    private fun readYaml() = this::class.java.classLoader.getResource("config.yml").readText()
}
