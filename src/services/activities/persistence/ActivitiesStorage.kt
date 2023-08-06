package de.moritzbruder.services.activities.persistence

import de.moritzbruder.services.activities.model.Activity
import de.moritzbruder.services.activities.model.TimeSuggestion

/**
 * Stores [Activity]s
 */
interface ActivitiesStorage {

    /**
     * Creates and returns a new activity with the given information
     */
    fun createActivity(
        placeId: String,
        title: String,
        description: String?,
        imageId: String?,
        imageThumbnail: String?,
        timeSuggestions: List<TimeSuggestion>,
        enabled: Boolean
    ): Activity

    /**
     * Marks the activity with [activityId] as deleted.
     */
    fun markActivityDeleted(activityId: String)

    /**
     * Returns the activity with the given id
     */
    fun getActivity(activityId: String, allowDeleted: Boolean = false): Activity?

    /**
     * Returns all of the activities that correspond to one of the places with the given [placeIds].
     * [filterEnabled]: If set to true, only places that are enabled are returned
     */
    fun getActivitiesForPlaces(placeIds: List<String>, filterEnabled: Boolean = false): List<Activity>

    /**
     * Enables/disables the activity with the given [activityId] based on [enabled]
     */
    fun setActivityEnabled(activityId: String, enabled: Boolean)

}