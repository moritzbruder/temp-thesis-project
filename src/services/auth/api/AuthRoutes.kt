package de.moritzbruder.services.auth.api

import de.moritzbruder.services.auth.actions.CreateAccountAction
import de.moritzbruder.services.auth.actions.InvalidCredentialsError
import de.moritzbruder.services.auth.actions.LoginAction
import de.moritzbruder.services.auth.actions.UserInput
import de.moritzbruder.services.auth.utilities.isEmailAddress
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

/**
 * Routes exposed by the auth service.
 */
fun Route.authRoutes(loginAction: LoginAction, createAccountAction: CreateAccountAction) {

    /**
     * Returns a JSON Web Token.
     * Authentication via headers "auth-email" and "auth-pw"
     */
    get("login") {
        val email = call.request.header("auth-email")
        val password = call.request.header("auth-pw")

        // Check if required headers are present
        if (email == null || password == null) {
            return@get call.respond(HttpStatusCode.BadRequest, "email and password headers required")
        }

        try {
            val token = loginAction(email, password)
            call.respondText(token)
        } catch (error: InvalidCredentialsError) {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }

    route("/user") {

        /**
         * Creates a new account.
         */
        post {
            val input = call.receiveOrNull<UserInput>()
                ?: return@post call.respond(HttpStatusCode.BadRequest)

            // Validate input being a well-formed email address
            if (!input.email.isEmailAddress()) {
                return@post call.respond(HttpStatusCode.BadRequest, "${input.email} is not a valid email address.")
            }
            if (input.password.length < 8) {
                return@post call.respond(HttpStatusCode.BadRequest, "password is too short")
            }

            createAccountAction(input)

            // Return 201
            call.respond(HttpStatusCode.Created)
        }

    }
}