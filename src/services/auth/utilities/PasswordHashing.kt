package de.moritzbruder.services.auth.utilities

import org.apache.commons.codec.binary.Hex
import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Crypto library wrapper used to easily salt & hash passwords as well as check the validity of input passwords.
 */
object PasswordHashing {

    private val digest = MessageDigest.getInstance("SHA-256")

    fun hash(clearPassword: String): HashedPassword {
        val salt =
            generateSalt()
        val combined =
            combinePasswordAndSalt(
                clearPassword,
                salt
            )
        val hash = Hex.encodeHexString(digest.digest(combined.toByteArray()))

        return HashedPassword(
            hash = hash,
            salt = salt
        )
    }

    fun check(inputPassword: String, password: HashedPassword): Boolean {
        return check(inputPassword, password.salt, password.hash)
    }

    fun check(inputPassword: String, salt: String, hash: String): Boolean {
        val combined =
            combinePasswordAndSalt(
                inputPassword,
                salt
            )
        val inputHash = Hex.encodeHexString(digest.digest(combined.toByteArray()))

        return hash == inputHash

    }

    private fun generateSalt(): String {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)

        val md = MessageDigest.getInstance("SHA-512")
        md.update(salt)

        return Hex.encodeHexString(salt)

    }

    private fun combinePasswordAndSalt(password: String, salt: String) = "$salt:$password"

}

data class HashedPassword(
    val hash: String,
    val salt: String
)