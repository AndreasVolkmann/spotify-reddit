package me.avo.spottit.service

import me.avo.spottit.model.RedditTrack

interface RedditService {

    fun getTracks(): List<RedditTrack>

    fun update(amountTaken: Int)

    val isDone: Boolean

}