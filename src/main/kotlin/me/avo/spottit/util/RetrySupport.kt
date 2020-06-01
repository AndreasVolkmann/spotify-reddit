package me.avo.spottit.util

import com.wrapper.spotify.exceptions.detailed.NotFoundException
import com.wrapper.spotify.requests.IRequest
import org.slf4j.Logger

interface RetrySupport {

    val logger: Logger

    val stackSize: Int get() = 5

    fun mapTimeout(ex: Exception): Long = 2000

    /**
     * Executes the request with retry support.
     */
    fun <T, X> IRequest.Builder<T, X>.execute(): T = retry {
        build().execute()
    }
}

inline fun <T> RetrySupport.retry(block: () -> T): T {
    var stack = 0
    do try {
        return block()
    }
    catch (ex: Exception) {
        if (ex is NotFoundException) {
            throw ex // should be handled by call site
        }
        logger.error("Exception encountered in retry code.", ex)
        stack++
        val timeout = mapTimeout(ex)
        Thread.sleep(timeout)
    }
    while (stack < stackSize)

    throw IllegalStateException("Retry did not return anything.")
}

