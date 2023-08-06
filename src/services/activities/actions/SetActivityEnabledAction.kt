package de.moritzbruder.services.activities.actions

import de.moritzbruder.services.activities.actions.base.ActivityContextAction
import de.moritzbruder.services.activities.persistence.ActivitiesStorage
import de.moritzbruder.services.places.PlacesService

class SetActivityEnabledAction(
    placesService: PlacesService,
    private val storage: ActivitiesStorage
) : ActivityContextAction(placesService, storage) {

    operator fun invoke(activityId: String, enabled: Boolean, userId: String) {
        val activity = getActivityIfOwner(activityId, userId)
        this.storage.setActivityEnabled(activity.id, enabled)

    }

}