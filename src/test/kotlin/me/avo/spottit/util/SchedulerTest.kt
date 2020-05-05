package me.avo.spottit.util

import me.avo.spottit.model.Schedule
import me.avo.spottit.util.Scheduler.shouldExecute
import org.junit.jupiter.api.Test
import strikt.api.expect
import strikt.assertions.isFalse
import strikt.assertions.isTrue
import java.util.*

internal class SchedulerTest {

    @Test fun `shouldExecute weekday`() {
        val dayOfWeek = getCalendar(Calendar.DAY_OF_WEEK)
        expect {
            that(shouldExecute(schedule(dayOfWeek))).isTrue()
            that(shouldExecute(schedule(dayOfWeek + 1))).isFalse()
        }
    }

    @Test fun `shouldExecute dayOfMonth`() {
        val dayOfMonth = getCalendar(Calendar.DAY_OF_MONTH)
        expect {
            that(shouldExecute(schedule(null, dayOfMonth))).isTrue()
            that(shouldExecute(schedule(null, dayOfMonth + 1))).isFalse()
        }
    }

    private fun schedule(dayOfWeek: Int? = null, dayOfMonth: Int? = null) = Schedule(dayOfWeek, dayOfMonth)

}