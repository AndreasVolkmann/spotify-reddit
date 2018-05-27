package me.avo.spottit

import com.wrapper.spotify.model_objects.specification.Track

fun track(builder: Track.Builder.() -> Unit): Track = Track.Builder().apply(builder).build()

fun track(id: String, builder: Track.Builder.() -> Unit = {}): Track = Track.Builder()
    .setId(id).apply(builder).build()

fun makeTracks(amount: Int, startFrom: Int = 0): List<Track> = (startFrom until amount + startFrom).map {
    track {
        setId(it.toString())
        setName(it.toString())
    }
}