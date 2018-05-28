package me.avo.spottit.model

import net.dean.jraw.models.SubredditSort
import net.dean.jraw.models.TimePeriod

data class Playlist(
    val id: String,
    val userId: String,
    val maxSize: Int,
    val subreddit: String,
    val sort: SubredditSort,
    val timePeriod: TimePeriod,
    val minimumUpvotes: Int?,
    val isStrictMix: Boolean
)