package de.moritzbruder.services.activities.actions

import de.moritzbruder.services.activities.actions.base.ActivityContextAction
import de.moritzbruder.services.activities.model.Activity
import de.moritzbruder.services.activities.persistence.ActivitiesStorage
import de.moritzbruder.services.places.PlacesService

class GetActivityAction(
    placesService: PlacesService,
    private val storage: ActivitiesStorage
) : ActivityContextAction(placesService, storage) {

    operator fun invoke(activityId: String, userId: String): Activity {
        return getActivityIfOwner(activityId, userId)
    }

}