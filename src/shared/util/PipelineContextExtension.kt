package de.moritzbruder.shared.util

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.pipeline.*

/**
 * Convenience method for finishing a pipeline while returning a specific status code.
 */
suspend fun PipelineContext<Unit, ApplicationCall>.finishAndRespond(
    statusCode: HttpStatusCode,
    message: String? = null
) {
    this.finish()
    if (message != null) {
        call.respond(statusCode, message)
    } else {
        call.respond(statusCode)
    }
}