package de.moritzbruder.services.activities.actions

import de.moritzbruder.services.activities.model.Activity
import de.moritzbruder.services.activities.model.TimeSuggestion
import de.moritzbruder.services.activities.model.toDaysInt
import de.moritzbruder.services.activities.persistence.ActivitiesStorage
import de.moritzbruder.services.images.ImagesService
import de.moritzbruder.services.places.PlacesService

data class ActivityInput(
    val placeId: String,
    val title: String,
    val description: String?,
    val timeSuggestions: List<TimeSuggestionInput>,
    val imageData: String?
)


data class TimeSuggestionInput(
    val days: List<Int>,
    val minuteOfDay: Short
)

fun List<TimeSuggestionInput>.toTimeslots(): List<TimeSuggestion> {
    return this.map { TimeSuggestion(it.days.toDaysInt(), it.minuteOfDay) }
}

class CreateActivityAction(
    private val imagesService: ImagesService,
    private val placesService: PlacesService,
    private val storage: ActivitiesStorage
) {
    operator fun invoke(userId: String, input: ActivityInput): Activity {
        // Check if user is allowed to create an activity at this place
        if (!placesService.checkUserPlaceOwnership(userId, input.placeId).get()) {
            throw UserNotOwnerError()
        }

        // Store image
        val image = input.imageData?.let { imagesService.storeImage(it).get() }

        // Store new activity in database
        val newActivity = storage.createActivity(
            placeId = input.placeId,
            title = input.title,
            description = input.description,
            imageId = image?.imageId,
            imageThumbnail = image?.thumbnailBase64,
            timeSuggestions = input.timeSuggestions.toTimeslots(),
            enabled = true
        )

        return newActivity
    }
}

class UserNotOwnerError : Error()