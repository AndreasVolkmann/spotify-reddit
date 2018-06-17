package me.avo.spottit.util

import me.avo.spottit.model.Schedule
import me.avo.spottit.util.Scheduler.shouldExecute
import org.amshove.kluent.shouldBe
import org.junit.jupiter.api.Test
import java.util.*

internal class SchedulerTest {

    @Test fun `shouldExecute weekday`() {
        val dayOfWeek = getCalendar(Calendar.DAY_OF_WEEK)
        shouldExecute(schedule(dayOfWeek)) shouldBe true
        shouldExecute(schedule(dayOfWeek + 1)) shouldBe false
    }

    @Test fun `shouldExecute dayOfMonth`() {
        val dayOfMonth = getCalendar(Calendar.DAY_OF_MONTH)
        shouldExecute(schedule(null, dayOfMonth)) shouldBe true
        shouldExecute(schedule(null, dayOfMonth + 1)) shouldBe false
    }

    private fun schedule(dayOfWeek: Int? = null, dayOfMonth: Int? = null) = Schedule(dayOfWeek, dayOfMonth)

}