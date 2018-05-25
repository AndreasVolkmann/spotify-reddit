package me.avo.spottit

import com.wrapper.spotify.model_objects.specification.Track

fun track(builder: Track.Builder.() -> Unit): Track = Track.Builder().apply(builder).build()