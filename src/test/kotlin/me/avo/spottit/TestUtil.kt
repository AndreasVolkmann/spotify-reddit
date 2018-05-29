package me.avo.spottit

import com.wrapper.spotify.model_objects.specification.Track
import me.avo.spottit.model.RedditTrack

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
    flair: String? = null
) = RedditTrack(artist, title, mix, extraInformation, flair)