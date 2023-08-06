package de.moritzbruder.services.auth

import de.moritzbruder.services.Service
import de.moritzbruder.services.auth.actions.CreateAccountAction
import de.moritzbruder.services.auth.actions.LoginAction
import de.moritzbruder.services.auth.api.authRoutes
import de.moritzbruder.services.auth.persistence.implementation.ExposedAuthStorage
import io.ktor.routing.*
import org.jetbrains.exposed.sql.Database

/**
 * Monolithic implementation of [AuthServiceInterface]
 */

class AuthService(private val db: Database) : Service {
    val storage = ExposedAuthStorage(db)
    override fun routes(): Routing.() -> Unit = {
        authRoutes(
            LoginAction(storage),
            CreateAccountAction(storage)
        )
    }
}