package services.auth.persistence

import createTestDb
import de.moritzbruder.services.auth.persistence.implementation.ExposedAuthStorage
import de.moritzbruder.services.auth.persistence.implementation.ExposedUser
import de.moritzbruder.services.auth.utilities.HashedPassword
import dropTestDb
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ExposedAuthStorageTest {

    private lateinit var db: Database

    @BeforeTest
    fun createDb() {
        db = createTestDb()
    }

    @AfterTest
    fun dropDb() {
        dropTestDb(db)
    }

    @Test
    fun canStoreUsers() {
        val storage = ExposedAuthStorage(db)
        storage.storeUser("John", "Doe", "john@doe.com", HashedPassword("test123", "salt"))
        transaction(db) {
            val users = ExposedUser.all().toList()
            assertEquals(users.size, 1)
            assertEquals("John", users[0].firstName)
            assertEquals("john@doe.com", users[0].email)
        }
    }

    @Test
    fun canFindUserByEmail() {
        val storage = ExposedAuthStorage(db)
        storage.storeUser("John", "Doe", "john@doe.com", HashedPassword("test123", "salt"))
        val found = storage.findUserByEmail("john@doe.com")
        assertEquals("john@doe.com", found?.email)

        val notFound = storage.findUserByEmail("john")
        assertEquals(null, notFound)
    }

}