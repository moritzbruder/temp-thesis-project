package de.moritzbruder.services.images.api

import de.moritzbruder.services.images.actions.GetImageAction
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import java.util.*

/**
 * Routes exposed by the images service.
 */
fun Route.imageRoutes(getImageAction: GetImageAction) {

    /**
     * Servers the image with the requested id.
     */
    get("/image/{imageId}") {
        val imageId = call.parameters["imageId"]
            ?: return@get call.respond(HttpStatusCode.BadRequest)

        val imageUuid: UUID
        try {
            imageUuid = UUID.fromString(imageId)
        } catch (e: Error) {
            return@get call.respond(HttpStatusCode.NotFound)
        }

        val image = getImageAction(imageUuid)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.apply { response.cacheControl(CacheControl.MaxAge(60 * 60 * 24 * 365)) }
            .respondBytes(image, contentType = ContentType.Image.PNG)
    }

}