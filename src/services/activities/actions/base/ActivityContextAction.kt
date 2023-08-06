package de.moritzbruder.services.activities.actions.base

import de.moritzbruder.services.activities.model.Activity
import de.moritzbruder.services.activities.persistence.ActivitiesStorage
import de.moritzbruder.services.places.PlacesService

open class ActivityContextAction(
    private val placesService: PlacesService,
    private val storage: ActivitiesStorage
) {

    fun getActivityIfOwner(activityId: String, userId: String): Activity {
        val activity = storage.getActivity(activityId, true)
            ?: throw ActivityNotFoundError()
        val isUserOwner = placesService.checkUserPlaceOwnership(userId, activity.placeId).get()
        if (!isUserOwner) {
            throw ActivityNotFoundError()
        }

        return activity
    }

}

class ActivityNotFoundError : Error("activity not found")