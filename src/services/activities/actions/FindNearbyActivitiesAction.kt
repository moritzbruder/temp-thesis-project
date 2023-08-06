package de.moritzbruder.services.activities.actions

import de.moritzbruder.services.activities.model.PublicActivity
import de.moritzbruder.services.activities.model.PublicPlace
import de.moritzbruder.services.activities.model.TimeSuggestion
import de.moritzbruder.services.activities.persistence.ActivitiesStorage
import de.moritzbruder.services.places.PlacesService
import org.joda.time.DateTime

class FindNearbyActivitiesAction(
    private val placesService: PlacesService,
    private val storage: ActivitiesStorage
) {

    operator fun invoke(lat: Double, lon: Double): List<PublicActivity> {
        val places = placesService.getPlaceDetailsInRadius(lat, lon, 10).get()
        val activities = storage.getActivitiesForPlaces(places.map { it.id }, filterEnabled = true)

        val placeMap = hashMapOf<String, PublicPlace>().apply {
            for (place in places) this[place.id] = PublicPlace(place)
        }

        val resultList = arrayListOf<PublicActivity>()
        for (activity in activities) {
            activity.timeSuggestions
            nextDateForSuggestion(activity.timeSuggestions)?.let {
                resultList.add(PublicActivity(activity, placeMap[activity.placeId]!!))
            }
        }

        return resultList
    }

    private fun nextDateForSuggestion(suggestions: List<TimeSuggestion>, weekOffset: Int = 0): DateTime? {
        var nextDate: DateTime? = null
        for (suggestion in suggestions) {
            for (day in suggestion.getAllDays()) {
                val possible = DateTime.now().plusWeeks(weekOffset).withDayOfWeek(day)
                    .withTime(suggestion.minuteOfDay / 60, suggestion.minuteOfDay % 60, 0, 0)
                if (possible.isAfterNow && (nextDate == null || possible.isBefore(nextDate))) {
                    nextDate = possible
                }
            }
        }

        return nextDate ?: if (weekOffset == 0) nextDateForSuggestion(suggestions, 1) else null

    }

}