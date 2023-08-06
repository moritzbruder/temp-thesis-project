package de.moritzbruder.services.places

import de.moritzbruder.services.places.model.Place
import java.util.concurrent.Future


/**
 * Service Responsible for managing places and their connection to osm.
 */
interface PlacesServiceInterface {

    /**
     * Returns whether the user with [userId] is the owner of the place with [placeId]
     */
    fun checkUserPlaceOwnership(userId: String, placeId: String): Future<Boolean>

    /**
     * Returns the ids of all places within [radius] (in km) of the point defined by [centerLat] & [centerLon]
     */
    fun getPlaceDetailsInRadius(centerLat: Double, centerLon: Double, radius: Int): Future<List<Place>>

    /**
     * Returns all places that are owned by the user with [userId]
     */
    fun getPlacesOwnedByUser(userId: String): Future<List<String>>

}