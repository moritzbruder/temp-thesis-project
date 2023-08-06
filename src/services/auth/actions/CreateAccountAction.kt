package de.moritzbruder.services.auth.actions

import de.moritzbruder.services.auth.persistence.AuthStorage
import de.moritzbruder.services.auth.utilities.PasswordHashing

/**
 * Holds the information required to create an account.
 */
data class UserInput(
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String
)

class CreateAccountAction(private val authStorage: AuthStorage) {

    operator fun invoke(input: UserInput): String {
        // Hash password
        val password = PasswordHashing.hash(input.password)
        val userId = authStorage.storeUser(input.firstName, input.lastName, input.email, password)

        return userId
    }

}