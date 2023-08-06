package de.moritzbruder.services.places.persistence

import de.moritzbruder.services.places.model.CoordinateBox
import de.moritzbruder.services.places.model.Place
import de.moritzbruder.services.places.model.PlaceCategory
import de.moritzbruder.services.places.model.PlaceOsmLink

/**
 * Stores [Place] instances as well as [PlaceOsmLink]s
 */
interface PlaceStorage {

    /**
     * Creates a new place with the given information. Returns the new place
     */
    fun createPlace(
        owner: String,
        name: String,
        description: String,
        category: PlaceCategory,
        pictureId: String,
        pictureThumbnail: String
    ): Place

    /**
     * Stores an [PlaceOsmLink] for the place with the given [placeId]
     */
    fun assignPlaceWithOsmEntity(placeId: String, osmLink: PlaceOsmLink)

    /**
     * Returns the [Place] with the given [placeId] or null if id not found.
     */
    fun findPlaceById(placeId: String): Place?

    /**
     * Returns all [Place]s owned by the user with the id [ownerId].
     */
    fun findPlacesByOwner(ownerId: String): List<Place>

    /**
     * Returns all [Place]s which have a osm link whose location is in the given box.
     */
    fun findPlacesInCoordinateBox(coordinateBox: CoordinateBox): List<Place>

}