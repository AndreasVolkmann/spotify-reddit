package me.avo.spottit.util

import me.avo.spottit.model.Schedule
import org.slf4j.LoggerFactory
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.DAY_OF_WEEK

object Scheduler {

    fun shouldExecute(schedule: Schedule): Boolean {
        val (dayOfWeek, dayOfMonth) = schedule
        dayOfMonth?.let {
            val current = getCalendar(DAY_OF_MONTH)
            if (dayOfMonth != current) {
                return logReason("Day of month should be $dayOfMonth, is $current")
            }
        }

        dayOfWeek?.let {
            val current = getCalendar(DAY_OF_WEEK)
            if (dayOfWeek != current) {
                return logReason("Day of week should be $dayOfWeek, is $current")
            }
        }

        return true
    }

    private fun logReason(reason: String): Boolean {
        logger.info(
            "According to the specified schedule, this configuration should not be executed. $reason"
        )
        return false
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

}
