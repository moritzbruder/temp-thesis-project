package services.auth.utilities

import de.moritzbruder.services.auth.utilities.PasswordHashing
import kotlin.test.Test
import kotlin.test.assertEquals

class PasswordHashingTest {

    @Test
    fun passwordHashing() {
        val hashed = PasswordHashing.hash("test123")
        assertEquals(true, PasswordHashing.check("test123", hashed))
        assertEquals(false, PasswordHashing.check("nope", hashed))
    }

}