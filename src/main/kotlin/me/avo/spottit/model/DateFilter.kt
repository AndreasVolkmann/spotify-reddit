package me.avo.spottit.model

import java.util.*

data class DateFilter(
    val startingFrom: Date?,
    val maxDistance: Date?
)
