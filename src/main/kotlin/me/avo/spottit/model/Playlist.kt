package me.avo.spottit.model

import net.dean.jraw.models.SubredditSort
import net.dean.jraw.models.TimePeriod

data class Playlist(
    val id: String,
    val maxSize: Int,
    val subreddit: String,
    val sort: SubredditSort,
    val timePeriod: TimePeriod,
    val minimumUpvotes: Int?,
    val isStrictMix: Boolean,
    val tagFilter: TagFilter,
    val dateFilter: DateFilter,
    val isPrivate: Boolean
) {

    override fun toString(): String = "Playlist(id=${id}, subReddit=${subreddit})"
}
