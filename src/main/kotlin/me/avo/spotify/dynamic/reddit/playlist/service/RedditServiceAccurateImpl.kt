package me.avo.spotify.dynamic.reddit.playlist.service

import me.avo.spotify.dynamic.reddit.playlist.model.Playlist
import me.avo.spotify.dynamic.reddit.playlist.model.RedditTrack
import net.dean.jraw.http.OkHttpNetworkAdapter
import net.dean.jraw.http.UserAgent
import net.dean.jraw.models.Submission
import net.dean.jraw.models.SubredditSort
import net.dean.jraw.models.TimePeriod
import net.dean.jraw.oauth.Credentials
import net.dean.jraw.oauth.OAuthHelper
import net.dean.jraw.references.SubredditReference
import org.slf4j.LoggerFactory
import java.util.*
/*
class RedditServiceAccurateImpl(
    clientId: String,
    clientSecret: String,
    deviceName: String,
    val playlist: Playlist
) : RedditService {

    private val credentials = Credentials.userless(clientId, clientSecret, UUID.fromString(deviceName))
    private val userAgent = UserAgent("bot", "me.avo.spotify.dynamic.reddit.playlist", "v0.1", "idajuul")
    private val adapter = OkHttpNetworkAdapter(userAgent)
    private val reddit = OAuthHelper.automatic(adapter, credentials)
    private val subreddit = reddit.subreddit(playlist.subreddit)

    private val queue = LinkedList<Submission>()

    private val filterMap = mutableMapOf<Int, Int>()

    var done = false
        private set

    private val postFilter = playlist.postFilters.first()
    private val paginator = subreddit.getTracks(postFilter.sort, postFilter.timePeriod)

    private fun nextPage(): List<Submission> = paginator.next()
        .also { println("Page contains ${it.size} tracks") }
        //.map(::submissionToRedditTrack)
        .also { queue.addAll(it) }

    override fun getTracks(playlist: Playlist): List<RedditTrack> {
        val tracks = mutableListOf<RedditTrack>()
        val key = playlist.postFilters.indexOf(postFilter)
        val currentSize = filterMap[key] ?: 0
        val limit = Math.min(postFilter.limit, playlist.maxSize)
        while (tracks.size + currentSize < limit) {
            if (queue.isEmpty()) {
                nextPage()
                if (paginator.pageNumber >= maxPage || queue.isEmpty()) { // still empty after nextPage
                    logger.info("PostFilter done")
                    done = true
                    break
                }
            }
            val submission = queue.pop() // add tracks whilst within the limit
            if (!submission.isSelfPost && submission.linkFlairText !in flairsToExclude) {
                tracks.add(submissionToRedditTrack(submission))
            }
        }
        return tracks
    }

    fun update(size: Int) {
        val key = playlist.postFilters.indexOf(postFilter)
        filterMap.compute(key) { _, v -> size + (v ?: 0) }
    }

    private fun submissionToRedditTrack(submission: Submission): RedditTrack {
        val fixedTitle = submission.title.fixChars()
        val extraInformation = fixedTitle.getExtraInformation()
        val mix = fixedTitle.getMix()
        val fullTitle = (extraInformation + mix).fold(fixedTitle) { acc, s -> acc.replace(s, "").trim() }
        return RedditTrack(
            artist = fullTitle.substringBefore("-").trim(),
            title = fullTitle.substringAfter("-").trim(),
            mix = mix.firstOrNull()?.removePrefixSuffix(),
            extraInformation = (extraInformation + mix.drop(1)).map { it.removePrefixSuffix() },
            flair = submission.linkFlairText
        )
    }

    private fun SubredditReference.getTracks(sort: SubredditSort, timePeriod: TimePeriod) = posts()
        .sorting(sort)
        .limit(limit)
        .timePeriod(timePeriod)
        .build()

    private val limit = 50
    private val maxPage = 10

    private val flairsToExclude = listOf("Mix", "Liveset", "Radio Show", "Album", "Upcoming AMA", "Concluded", "RIP")

    private fun String.fixChars() = replace("&amp;", "&")

    private fun String.getMix() = getEnclosedText("(", ")")

    private fun String.getExtraInformation(): List<String> = getEnclosedText("[", "]")

    private fun String.getEnclosedText(start: String, end: String) = Regex("\\$start.*?\\$end")
        .findAll(this)
        .map(MatchResult::value)
        .toList()

    private val prefixChars = listOf("(", "[")
    private val suffixChars = listOf(")", "]")

    private fun String.removePrefixSuffix() = prefixChars.fold(this) { acc, s -> acc.removePrefix(s) }
        .let { suffixChars.fold(it) { acc, s -> acc.removeSuffix(s) } }

    private val logger = LoggerFactory.getLogger(this::class.java)

}*/