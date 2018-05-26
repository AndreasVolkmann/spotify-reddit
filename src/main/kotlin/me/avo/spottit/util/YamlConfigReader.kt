package me.avo.spottit.util

import me.avo.spottit.model.Configuration
import me.avo.spottit.model.Playlist
import net.dean.jraw.models.SubredditSort
import net.dean.jraw.models.TimePeriod
import org.yaml.snakeyaml.Yaml

@Suppress("UNCHECKED_CAST")
object YamlConfigReader {

    fun read(yaml: String): Configuration {
        val data = Yaml().load<Map<String, Any?>>(yaml)
        val playlists = data["playlists"] as? List<Map<String, Any>> ?: listOf()
        val spotifyCredentials = data["spotify"] as? Map<String, Any?> ?: mapOf()
        val userId = data["userId"].toString()

        return Configuration(
            userId = userId,
            playlists = playlists.map {
                Playlist(
                    id = it["id"].toString(),
                    userId = userId,
                    maxSize = it["maxSize"]?.toString()?.toInt() ?: 50,
                    subreddit = it["subreddit"].toString(),
                    sort = SubredditSort.valueOf(it["sort"].toString()),
                    timePeriod = TimePeriod.valueOf(it["timePeriod"].toString()),
                    minimumUpvotes = it["minUpvotes"]?.toString()?.toInt()
                )
            },
            flairsToExclude = data["flairsToExclude"] as? List<String> ?: listOf(),
            minimumLength = data["minimumLength"]?.toString()?.toInt() ?: 0,
            spotifyUser = spotifyCredentials.getEnvOrYaml("SPOTIFY_USER", "user").toString(),
            spotifyPass = spotifyCredentials.getEnvOrYaml("SPOTIFY_PASS", "pass").toString()
        )
    }

    private fun Map<String, Any?>.getEnvOrYaml(envKey: String, yamlKey: String): Any =
        System.getenv(envKey) ?: get(yamlKey) ?: failRequiredProp(yamlKey)

    private fun failRequiredProp(key: String): Nothing =
        throw IllegalArgumentException("The yaml config requires $key to be defined")

}