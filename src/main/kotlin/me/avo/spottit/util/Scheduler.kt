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
                log("Day of month should be $dayOfMonth, is $current")
                return false
            }
        }

        dayOfWeek?.let {
            val current = getCalendar(DAY_OF_WEEK)
            if (dayOfWeek != current) {
                log("Day of week should be $dayOfWeek, is $current")
                return false
            }
        }

        return true
    }


    private fun log(reason: String) = logger.info(
        "According to the specified schedule, this configuration should not be executed. $reason"
    )

    private val logger = LoggerFactory.getLogger(this::class.java)

}
