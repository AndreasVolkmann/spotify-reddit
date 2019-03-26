package me.avo.spottit.util

interface RetrySupport {

    val stackSize: Int

    fun mapTimeout(ex: Exception): Int

    fun <T> retry(block: () -> T) = retry(block, 0)

    private fun <T> retry(block: () -> T, stack: Int = 0): T = try {
        block()
    } catch (ex: Exception) {
        val timeout = mapTimeout(ex)
        when {
            stack < stackSize -> {
                val waitForSeconds = timeout / 1000L
                Thread.sleep(waitForSeconds)
                retry(block, stack + 1)
            }
            else -> throw ex
        }
    }
}
