package de.moritzbruder.services.places.api

import de.moritzbruder.services.places.actions.CreatePlaceAction
import de.moritzbruder.services.places.actions.LinkOsmPlaceAction
import de.moritzbruder.services.places.actions.LinkOsmPlaceInput
import de.moritzbruder.services.places.actions.PlaceInput
import de.moritzbruder.services.places.model.Place
import de.moritzbruder.services.places.persistence.PlaceStorage
import de.moritzbruder.shared.auth.userId
import de.moritzbruder.shared.util.finishAndRespond
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

/**
 * Routes exposed by the places service
 */
fun Route.placesRoutes(
    createPlaceAction: CreatePlaceAction,
    linkOsmPlaceAction: LinkOsmPlaceAction,
    storage: PlaceStorage
) {

    route("/place") {

        authenticate {

            post {
                val input = call.receiveOrNull<PlaceInput>()
                    ?: return@post call.respond(BadRequest)

                val newPlace = createPlaceAction(input, call.userId)

                call.respond(Created, newPlace)
            }

            /**
             * Returns a list of [Place]s owned by the user who authenticated the call.
             */
            get {
                val places = storage.findPlacesByOwner(call.userId)
                call.respond(places)
            }

            /**
             * Route to a specific [Place]. Addressed by [Place.id] UUID.
             */
            route("/{placeId}") {

                get {
                    val placeId = call.parameters["placeId"] ?: return@get finishAndRespond(BadRequest)
                    val place = storage.findPlaceById(placeId)
                    if (place == null || place.owner != call.userId) {
                        return@get call.respond(NotFound)
                    }
                    call.respond(place)
                }

                put("/request-link") {
                    val placeId = call.parameters["placeId"] ?: return@put finishAndRespond(BadRequest)
                    val input = call.receiveOrNull<OsmLinkRequestBody>() ?: return@put finishAndRespond(BadRequest)
                    val osmId = input.osmId.toLongOrNull() ?: return@put finishAndRespond(BadRequest)
                    val osmType = input.osmType.getOrNull(0) ?: return@put finishAndRespond(BadRequest)

                    linkOsmPlaceAction(LinkOsmPlaceInput(osmId, osmType, call.userId, placeId))

                    call.respond(OK)

                }

            }
        }

    }

}

data class OsmLinkRequestBody(
    val osmType: String,
    val osmId: String
)