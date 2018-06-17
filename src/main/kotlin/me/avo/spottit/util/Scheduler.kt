package me.avo.spottit.util

import me.avo.spottit.model.Schedule
import org.slf4j.LoggerFactory
import java.util.Calendar.*

object Scheduler {

    fun shouldExecute(schedule: Schedule): Boolean {
        val (weekday, dayOfMonth) = schedule
        dayOfMonth?.let {
            val current = getCalendar(DAY_OF_MONTH)
            if (dayOfMonth != current) {
                log("Day of month should be $dayOfMonth, is $current")
                return false
            }
        }

        weekday?.let {
            val current = getCalendar(DAY_OF_WEEK)
            if (getWeekDay(weekday) != current) {
                log("Day of week should be $weekday, is $current")
                return false
            }
        }

        return true
    }

    private fun getWeekDay(name: String) = when (name) {
        "MONDAY", "MON", "2" -> MONDAY
        "TUESDAY", "TUE", "3" -> TUESDAY
        "WEDNESDAY", "WED", "4" -> WEDNESDAY
        "THURSDAY", "THU", "5" -> THURSDAY
        "FRIDAY", "FRI", "6" -> FRIDAY
        "SATURDAY", "SAT", "7" -> SATURDAY
        "SUNDAY", "SUN", "1" -> SUNDAY
        else -> throw IllegalArgumentException("Unrecognized week day '$name'")
    }

    private fun log(reason: String) = logger.info(
        "According to the specified schedule, this configuration should not be executed. $reason"
    )

    private val logger = LoggerFactory.getLogger(this::class.java)

}