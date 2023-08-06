package de.moritzbruder.services.activities.model

/**
 * Activity shown to user as a suggestion of "things to do" in a city/area.
 */
class Activity(

    /**
     * UUID of the activity
     */
    val id: String,

    /**
     * Id of the place suggesting this activity.
     */
    val placeId: String,

    /**
     * Friendly name of the activity
     */
    val title: String,

    /**
     * Id used to retrieve the full version of this activity's image.
     */
    val imageId: String?,

    /**
     * Low-res base64 version of this activity's image.
     */
    val imageThumbnail: String?,

    /**
     * Extended description of this activity
     */
    val description: String?,

    /**
     * Determines whether the activity is actively recommended or not.
     */
    val enabled: Boolean,

    /**
     * Times suggested at which users can perform the activity.
     */
    val timeSuggestions: List<TimeSuggestion>

)