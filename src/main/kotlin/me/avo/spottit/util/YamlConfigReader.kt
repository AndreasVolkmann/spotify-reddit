package me.avo.spottit.util

import me.avo.spottit.model.Configuration
import me.avo.spottit.model.Playlist
import me.avo.spottit.model.TagFilter
import net.dean.jraw.models.SubredditSort
import net.dean.jraw.models.TimePeriod
import org.yaml.snakeyaml.Yaml

@Suppress("UNCHECKED_CAST")
object YamlConfigReader {

    fun read(yaml: String): Configuration {
        val data = Yaml().load<Map<String, Any?>>(yaml)
        val playlists = data["playlists"] as? List<Map<String, Any>> ?: listOf()
        val userId = data["userId"].toString()

        return Configuration(
            userId = userId,
            playlists = playlists.map {
                val tagFilter = it["tagFilter"] as? Map<String, List<String>> ?: mapOf()
                Playlist(
                    id = it["id"].toString(),
                    userId = userId,
                    maxSize = it["maxSize"]?.toString()?.toInt() ?: 50,
                    subreddit = it["subreddit"].toString(),
                    sort = SubredditSort.valueOf(it["sort"].toString()),
                    timePeriod = TimePeriod.valueOf(it["timePeriod"].toString()),
                    minimumUpvotes = it["minUpvotes"]?.toString()?.toInt(),
                    isStrictMix = it["isStrictMix"]?.toString()?.toBoolean() ?: false,
                    tagFilter = TagFilter(
                        include = tagFilter["include"] ?: listOf(),
                        includeExact = tagFilter["includeExact"] ?: listOf(),
                        exclude = tagFilter["exclude"] ?: listOf(),
                        excludeExact = tagFilter["excludeExact"] ?: listOf()
                    )
                )
            },
            flairsToExclude = data["flairsToExclude"] as? List<String> ?: listOf(),
            minimumLength = data["minimumLength"]?.toString()?.toInt() ?: 0,
            rateLimitInMs = (System.getenv("RATE_LIMIT") ?: data["rateLimit"]?.toString())?.toLong() ?: 500
        )
    }

}