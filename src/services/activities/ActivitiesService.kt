package de.moritzbruder.services.activities

import de.moritzbruder.services.Service
import de.moritzbruder.services.ServiceRef
import de.moritzbruder.services.activities.actions.*
import de.moritzbruder.services.activities.api.activitiesRoutes
import de.moritzbruder.services.activities.persistence.ActivitiesStorage
import de.moritzbruder.services.activities.persistence.implementation.ExposedActivitiesStorage
import de.moritzbruder.services.images.ImagesService
import de.moritzbruder.services.places.PlacesService
import io.ktor.routing.*
import org.jetbrains.exposed.sql.Database

/**
 * Monolithic implementation of [ActivitiesServiceInterface]
 */
class ActivitiesService(
    db: Database,
    private val imagesService: ServiceRef<ImagesService>,
    private val placesService: ServiceRef<PlacesService>
) : Service, ActivitiesServiceInterface {

    val storage: ActivitiesStorage = ExposedActivitiesStorage(db)
    override fun routes(): Routing.() -> Unit = {
        activitiesRoutes(
            CreateActivityAction(imagesService.service, placesService.service, storage),
            FindNearbyActivitiesAction(placesService.service, storage),
            GetActivityAction(placesService.service, storage),
            DeleteActivityAction(placesService.service, storage),
            SetActivityEnabledAction(placesService.service, storage)
        )
    }

}