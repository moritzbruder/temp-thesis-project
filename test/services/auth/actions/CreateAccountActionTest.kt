package services.auth.actions

import de.moritzbruder.services.auth.actions.CreateAccountAction
import de.moritzbruder.services.auth.actions.UserInput
import de.moritzbruder.services.auth.persistence.AuthStorage
import de.moritzbruder.services.auth.utilities.HashedPassword
import de.moritzbruder.services.auth.utilities.PasswordHashing
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Test
import kotlin.test.assertEquals

class CreateAccountActionTest {

    val authStorage = mockk<AuthStorage>()

    @Test
    fun createsUser() {
        mockkObject(PasswordHashing)
        val hpw = HashedPassword("hash", "salt")
        every { authStorage.storeUser("test", "mail", "test@mail.com", hpw) } returns "user-id"
        every { PasswordHashing.hash("pw") } returns hpw

        val action = CreateAccountAction(authStorage)
        val result = action(UserInput("test@mail.com", "test", "mail", "pw"))
        verify { authStorage.storeUser("test", "mail", "test@mail.com", hpw) }
        assertEquals("user-id", result)
    }

}