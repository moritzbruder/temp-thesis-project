package de.moritzbruder.services.activities.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.DayOfWeek

/**
 * A specific time at specific days of the week.
 */
class TimeSuggestion(
    /**
     * Days of the week
     */
    private val days: Int,
    /**
     * Minute of the day
     */
    val minuteOfDay: Short
) {

    companion object {

        /**
         * Creates a single [TimeSuggestion] from a formatted string.
         */
        fun fromString(input: String): TimeSuggestion {
            val split = input.split(":")
            val days = split[0].toInt()
            val minuteOfDay = split[1].toShort()

            return TimeSuggestion(days, minuteOfDay)

        }

        /**
         * Creates a list of [TimeSuggestion]s from a formatted string.
         */
        fun listFromString(input: String): List<TimeSuggestion> {
            if (input.trim().isEmpty()) {
                return listOf()
            }
            return input.split(",").map { fromString(it.trim()) }

        }

    }

    /**
     * Returns whether the given date lies within the slots defined by this instance
     */
    fun includesDayOfWeek(day: Int): Boolean {
        return days shr (day - 1) and 1 == 1

    }

    /**
     * Returns the list of [DayOfWeek] values that this timeslot applies to.
     */
    @JsonProperty("days")
    fun getAllDays(): List<Int> {
        return DayOfWeek.values().filter { this.includesDayOfWeek(it.value) }.map { it.value }

    }

    /**
     * Returns a formatted string used to represent this instance.
     * Use [TimeSuggestion.fromString] to convert it back to a [TimeSuggestion].
     */
    override fun toString(): String {
        return "${this.days}:${this.minuteOfDay}"
    }

}

/**
 * Converts a list of integers representing days of the week (ints in range 1..7) to a single integer using its bytes
 * to indicate whether a day is included.
 */
fun Iterable<Int>.toDaysInt(): Int {
    var result = 0
    for (day in this) {
        result = result or (1 shl (day - 1))

    }
    return result
}

/**
 * Returns a formatted string used to represent this list of [TimeSuggestion]s.
 * Use [TimeSuggestion.listFromString] to convert it back to a [TimeSuggestion] list.
 */
fun List<TimeSuggestion>.toTimeslotString(): String {
    return this.joinToString(",")

}