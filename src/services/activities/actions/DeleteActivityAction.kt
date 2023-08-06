package de.moritzbruder.services.activities.actions

import de.moritzbruder.services.activities.actions.base.ActivityContextAction
import de.moritzbruder.services.activities.persistence.ActivitiesStorage
import de.moritzbruder.services.places.PlacesService

class DeleteActivityAction(
    placesService: PlacesService,
    private val storage: ActivitiesStorage
) : ActivityContextAction(placesService, storage) {

    operator fun invoke(activityId: String, userId: String) {
        val activity = getActivityIfOwner(activityId, userId)
        this.storage.markActivityDeleted(activity.id)

    }

}