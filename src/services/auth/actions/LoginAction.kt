package de.moritzbruder.services.auth.actions

import de.moritzbruder.services.auth.persistence.AuthStorage
import de.moritzbruder.services.auth.utilities.PasswordHashing
import de.moritzbruder.shared.auth.JwtAuthentication

class LoginAction(private val authStorage: AuthStorage) {

    /**
     * Attempts to create a new token with the email and password given
     */
    operator fun invoke(email: String, password: String): String {
        // Try to find user with email address
        val user = authStorage.findUserByEmail(email)
            ?: throw InvalidCredentialsError()

        // Check if password is valid
        val pwCheck = PasswordHashing.check(
            inputPassword = password,
            password = user.password
        )
        if (!pwCheck) throw InvalidCredentialsError()

        // Generate & return JWT
        return JwtAuthentication.makeToken(user.userId)
    }

}

class InvalidCredentialsError : Error()