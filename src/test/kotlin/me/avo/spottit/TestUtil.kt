package me.avo.spottit

import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.model.RedditTrack
import me.avo.spottit.model.TagFilter
import me.avo.spottit.util.YamlConfigReader
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

fun redditTrack(
    artist: String,
    title: String,
    mix: String? = null,
    extraInformation: List<String> = listOf(),
    flair: String? = null,
    url: String = "",
    created: Date = Date()
) = RedditTrack(artist, title, mix, extraInformation, flair, url, created)

fun getTestConfig() =
    TestUtil::class.java.classLoader.getResource("test_config.yml").readText().let(YamlConfigReader::read)

fun tagFilter(
    include: List<String> = listOf(),
    includeExact: List<String> = listOf(),
    exclude: List<String> = listOf(),
    excludeExact: List<String> = listOf()
) = TagFilter(include, includeExact, exclude, excludeExact)

object TestUtil