package me.avo.spottit.service.reddit

import me.avo.spottit.model.RedditTrack

/**
 * Reports done after [doneAfterTimes].
 */
class RedditServiceFake(private val doneAfterTimes: Int) : RedditService {

    private var counter = 0

    override fun getRedditTracks(): List<RedditTrack> {
        return listOf()
    }

    override fun setCurrentSize(size: Int) {
    }

    override fun isDone(): Boolean = counter++ >= doneAfterTimes

}