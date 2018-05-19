package me.avo.spotify.dynamic.reddit.playlist.model

import net.dean.jraw.models.SubredditSort
import net.dean.jraw.models.TimePeriod

data class PostFilter(
    val sort: SubredditSort,
    val timePeriod: TimePeriod,
    val limit: Int
)