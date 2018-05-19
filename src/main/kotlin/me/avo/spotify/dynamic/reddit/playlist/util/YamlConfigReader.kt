package me.avo.spotify.dynamic.reddit.playlist.util

import me.avo.spotify.dynamic.reddit.playlist.config.Configuration
import me.avo.spotify.dynamic.reddit.playlist.model.Playlist
import me.avo.spotify.dynamic.reddit.playlist.model.PostFilter
import net.dean.jraw.models.SubredditSort
import net.dean.jraw.models.TimePeriod
import org.yaml.snakeyaml.Yaml

@Suppress("UNCHECKED_CAST")
object YamlConfigReader {

    fun read(yaml: String): Configuration {
        val data = Yaml().load<Map<String, Any>>(yaml)

        val playlists = data["playlists"] as? List<Map<String, Any>> ?: listOf()

        val userId = data["userId"].toString()
        return Configuration(
            userId = userId,
            playlists = playlists.map {
                Playlist(
                    id = it["playlistId"].toString(),
                    userId = userId,
                    maxSize = it["maxSize"]?.toString()?.toInt() ?: 50,
                    subreddit = it["subreddit"].toString(),
                    postFilters = (it["postFilters"] as? List<Map<String, Any>> ?: listOf()).map {
                        PostFilter(
                            sort = SubredditSort.valueOf(it["sort"].toString()),
                            timePeriod = TimePeriod.valueOf(it["timePeriod"].toString()),
                            limit = it["limit"].toString().toInt()
                        )
                    }
                )
            }
        )
    }


}