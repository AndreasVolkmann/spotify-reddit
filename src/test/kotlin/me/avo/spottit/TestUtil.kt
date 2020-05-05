package me.avo.spottit

import com.wrapper.spotify.model_objects.specification.Album
import com.wrapper.spotify.model_objects.specification.ArtistSimplified
import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.model.*
import me.avo.spottit.util.YamlConfigReader
import net.dean.jraw.models.SubredditSort
import net.dean.jraw.models.TimePeriod
import java.net.URL
import java.util.*

fun track(builder: Track.Builder.() -> Unit): Track = Track.Builder().apply(builder).build()

fun track(id: String, builder: Track.Builder.() -> Unit = {}): Track = Track.Builder()
    .setId(id).apply(builder).build()

fun makeTracks(amount: Int, startFrom: Int = 0): List<Track> = (startFrom until amount + startFrom).map {
    track {
        setId(it.toString())
        setName(it.toString())
    }
}

fun makePlaylist() = Playlist("1", 2, "test", SubredditSort.TOP, TimePeriod.ALL, 0, true, TagFilter(listOf(), listOf(), listOf(), listOf()), DateFilter(null, null), true)

fun album(builder: Album.Builder.() -> Unit): Album = Album.Builder().apply(builder).build()

fun redditTrack(
    artist: String,
    title: String,
    mix: String? = null,
    extraInformation: List<String> = listOf(),
    flair: String? = null,
    url: URL? = null,
    created: Date = Date()
) = RedditTrack(artist, title, mix, extraInformation, flair, url, created)

fun getTestConfig() = TestUtil::class.java.classLoader.getResource("test_config.yml")!!
    .readText().let(YamlConfigReader::read)

fun tagFilter(
    include: List<String> = listOf(),
    includeExact: List<String> = listOf(),
    exclude: List<String> = listOf(),
    excludeExact: List<String> = listOf()
) = TagFilter(include, includeExact, exclude, excludeExact)


fun makeConfig(dateFilter: DateFilter): Configuration = getTestConfig().let {
    val pl = it.playlists.first().copy(dateFilter = dateFilter)
    it.copy(playlists = listOf(pl))
}

fun artist(name: String): ArtistSimplified = ArtistSimplified.Builder().setName(name).build()

object TestUtil
