package me.avo.spottit.model

data class TagFilter(
    val include: List<String>,
    val includeExact: List<String>,
    val exclude: List<String>,
    val excludeExact: List<String>
)