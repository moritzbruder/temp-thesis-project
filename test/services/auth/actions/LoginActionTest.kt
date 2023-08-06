package services.auth.actions

import de.moritzbruder.services.auth.actions.InvalidCredentialsError
import de.moritzbruder.services.auth.actions.LoginAction
import de.moritzbruder.services.auth.model.User
import de.moritzbruder.services.auth.persistence.AuthStorage
import de.moritzbruder.services.auth.utilities.HashedPassword
import de.moritzbruder.services.auth.utilities.PasswordHashing
import de.moritzbruder.shared.auth.JwtAuthentication
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import kotlin.test.Test
import kotlin.test.assertEquals

class LoginActionTest {

    val authStorage = mockk<AuthStorage>()

    @Test
    fun validLogin() {
        val user = User("123", "test", HashedPassword("hash", "salt"), null)
        mockkObject(PasswordHashing)
        mockkObject(JwtAuthentication)
        every { authStorage.findUserByEmail("test") } returns user
        every { PasswordHashing.check("pw", user.password) } returns true
        every { JwtAuthentication.makeToken(user.userId) } returns "test-token"

        val action = LoginAction(authStorage)

        val result = action.invoke("test", "pw")
        assertEquals("test-token", result)
        verify { authStorage.findUserByEmail("test") }
        verify { PasswordHashing.check("pw", user.password) }
        verify { JwtAuthentication.makeToken(user.userId) }
    }

    @Test(expected = InvalidCredentialsError::class)
    fun userDoesNotExist() {
        every { authStorage.findUserByEmail("test") } returns null
        val action = LoginAction(authStorage)
        action.invoke("test", "pw")
    }

    @Test(expected = InvalidCredentialsError::class)
    fun wrongPassword() {
        val user = User("123", "test", HashedPassword("hash", "salt"), null)
        mockkObject(PasswordHashing)
        every { authStorage.findUserByEmail("test") } returns user
        every { PasswordHashing.check("pw", user.password) } returns false

        val action = LoginAction(authStorage)
        action.invoke("test", "pw")
    }

}