package de.moritzbruder.services.auth.persistence

import de.moritzbruder.services.auth.model.User
import de.moritzbruder.services.auth.utilities.HashedPassword

/**
 * Stores users.
 */
interface AuthStorage {

    /**
     * Write user to database and return its new UUID.
     */
    fun storeUser(firstName: String, lastName: String, email: String, password: HashedPassword): String

    /**
     * Returns the user with the given email-address.
     */
    fun findUserByEmail(email: String): User?

}

