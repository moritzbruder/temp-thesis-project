package de.moritzbruder.services.activities.api

import de.moritzbruder.services.activities.actions.*
import de.moritzbruder.shared.auth.userId
import de.moritzbruder.shared.util.finishAndRespond
import de.moritzbruder.shared.util.tryReceive
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.response.*
import io.ktor.routing.*

/**
 * All routes exposed by the listings service
 */
fun Route.activitiesRoutes(
    createActivityAction: CreateActivityAction,
    findNearbyActivitiesAction: FindNearbyActivitiesAction,
    getActivityAction: GetActivityAction,
    deleteActivityAction: DeleteActivityAction,
    setActivityEnabledAction: SetActivityEnabledAction
) {

    route("/activity") {

        get("/nearby") {
            val latitude = call.request.queryParameters["lat"]?.toDoubleOrNull()
                ?: return@get finishAndRespond(BadRequest)
            val longitude = call.request.queryParameters["lon"]?.toDoubleOrNull()
                ?: return@get finishAndRespond(BadRequest)

            val nearbyActivitiesAction = findNearbyActivitiesAction(latitude, longitude)

            call.respond(nearbyActivitiesAction)

        }

        authenticate {

            post {
                val input = call.tryReceive<ActivityInput>()
                    ?: return@post call.respond(BadRequest)
                try {
                    val activity = createActivityAction(call.userId, input)
                    call.respond(Created, activity)
                } catch (e: UserNotOwnerError) {
                    call.respond(Unauthorized)
                }
            }

            route("/{activityId}") {
                get {
                    val activityId = call.parameters["activityId"]
                        ?: return@get finishAndRespond(BadRequest)
                    val actvity = getActivityAction(activityId, call.userId)
                    call.respond(actvity)
                }

                delete {
                    val activityId = call.parameters["activityId"]
                        ?: return@delete finishAndRespond(BadRequest)
                    deleteActivityAction(activityId, call.userId)
                    call.respond(OK)
                }

                put("/enabled") {
                    val activityId = call.parameters["activityId"]
                        ?: return@put finishAndRespond(BadRequest)
                    val enabled = call.tryReceive<Boolean>()
                        ?: return@put call.respond(BadRequest)
                    setActivityEnabledAction(activityId, enabled, call.userId)
                    call.respond(OK)
                }
            }
        }
    }
}