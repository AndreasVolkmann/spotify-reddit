package me.avo.spottit.service

import me.avo.spottit.model.Playlist
import me.avo.spottit.model.RedditCredentials
import me.avo.spottit.model.RedditTrack
import me.avo.spottit.util.SubmissionParser
import net.dean.jraw.http.OkHttpNetworkAdapter
import net.dean.jraw.http.UserAgent
import net.dean.jraw.models.Submission
import net.dean.jraw.models.SubredditSort
import net.dean.jraw.models.TimePeriod
import net.dean.jraw.oauth.Credentials
import net.dean.jraw.oauth.OAuthHelper
import net.dean.jraw.pagination.Paginator
import net.dean.jraw.references.SubredditReference
import org.slf4j.LoggerFactory
import java.util.*

class RedditServiceImpl(
    private val playlist: Playlist,
    private val flairsToExclude: List<String>,
    redditCredentials: RedditCredentials
) : RedditService {

    private val reddit by lazy {
        val (clientId, clientSecret, deviceName) = redditCredentials
        val userAgent = UserAgent("bot", "me.avo.spottit", "v0.2", "idajuul")
        val adapter = OkHttpNetworkAdapter(userAgent)
        val credentials = Credentials.userless(clientId, clientSecret, UUID.fromString(deviceName))
        OAuthHelper.automatic(adapter, credentials)
    }

    override var isDone = false
        private set

    override fun getTracks(): List<RedditTrack> {
        val validPosts = mutableListOf<Submission>()

        initializePaginator()

        while (currentSize + validPosts.size < playlist.maxSize) {
            val page = paginator.next()
            logger.info("Reading page ${paginator.pageNumber}")
            if (page.isEmpty() || paginator.pageNumber >= maxPage) {
                stop()
                break
            }
            page
                .filterNot { it.isSelfPost }
                .filterNot { it.linkFlairText in flairsToExclude }
                .filter { it.score > playlist.minimumUpvotes }
                .mapTo(validPosts) { it }
        }

        if (paginator.pageNumber >= maxPage || currentSize >= playlist.maxSize) {
            stop()
        }

        return validPosts.map(::parse)
    }

    override fun update(amountTaken: Int) {
        logger.info("Adding $amountTaken tracks")
        currentSize += amountTaken
    }

    private lateinit var paginator: Paginator<Submission>

    private var currentSize = 0

    private fun stop() {
        logger.info("Reddit pagination done")
        isDone = true
    }

    private fun initializePaginator() {
        if (!::paginator.isInitialized) {
            val subreddit = reddit.subreddit(playlist.subreddit)
            paginator = subreddit.getTracks(playlist.sort, playlist.timePeriod)
        }
    }

    private fun SubredditReference.getTracks(sort: SubredditSort, timePeriod: TimePeriod) = posts()
        .sorting(sort)
        .timePeriod(timePeriod)
        .build()

    private val maxPage = 20

    private val logger = LoggerFactory.getLogger(this::class.java)

    private fun parse(submission: Submission): RedditTrack =
        SubmissionParser.parse(submission.title, submission.linkFlairText)

}