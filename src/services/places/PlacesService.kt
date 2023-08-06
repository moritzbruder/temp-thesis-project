package de.moritzbruder.services.places

import de.moritzbruder.services.Service
import de.moritzbruder.services.ServiceRef
import de.moritzbruder.services.images.ImagesService
import de.moritzbruder.services.places.actions.CreatePlaceAction
import de.moritzbruder.services.places.actions.LinkOsmPlaceAction
import de.moritzbruder.services.places.api.placesRoutes
import de.moritzbruder.services.places.model.CoordinateBox
import de.moritzbruder.services.places.model.Place
import de.moritzbruder.services.places.persistence.PlaceStorage
import de.moritzbruder.services.places.persistence.implementation.ExposedPlaceStorage
import de.westnordost.osmapi.OsmConnection
import io.ktor.routing.*
import org.jetbrains.exposed.sql.Database
import java.util.concurrent.Future
import java.util.concurrent.FutureTask

/**
 * Monolithic implementation of [PlacesServiceInterface]
 */
class PlacesService(
    db: Database,
    private val imagesService: ServiceRef<ImagesService>,
    private val osmClient: OsmConnection
) : Service, PlacesServiceInterface {

    private val storage: PlaceStorage = ExposedPlaceStorage(db)

    override fun checkUserPlaceOwnership(userId: String, placeId: String): Future<Boolean> {
        return FutureTask {
            storage.findPlaceById(placeId)?.owner == userId

        }.apply { run() }
    }

    override fun getPlaceDetailsInRadius(centerLat: Double, centerLon: Double, radius: Int): Future<List<Place>> {
        return FutureTask {
            storage.findPlacesInCoordinateBox(CoordinateBox(centerLat, centerLon, radius * 1000.0))

        }.apply { run() }
    }

    override fun getPlacesOwnedByUser(userId: String): Future<List<String>> {
        return FutureTask {
            storage.findPlacesByOwner(userId).map { it.id }

        }.apply { run() }
    }

    override fun routes(): Routing.() -> Unit = {
        placesRoutes(
            CreatePlaceAction(storage, imagesService.service),
            LinkOsmPlaceAction(osmClient, storage),
            storage
        )
    }

}