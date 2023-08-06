package de.moritzbruder.services.auth.persistence.implementation

import de.moritzbruder.services.auth.model.User
import de.moritzbruder.services.auth.model.UserAddress
import de.moritzbruder.services.auth.persistence.AuthStorage
import de.moritzbruder.services.auth.utilities.HashedPassword
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * SQL-based implementation of [AuthStorage]
 */
class ExposedAuthStorage(val db: Database) : AuthStorage {

    init {
        transaction(db) {
            SchemaUtils.createMissingTablesAndColumns(
                UserTable,
                UserAddressTable
            )
        }
    }

    override fun storeUser(firstName: String, lastName: String, email: String, password: HashedPassword): String {
        return transaction(db) {
            ExposedUser.new {
                this.firstName = firstName
                this.lastName = lastName
                this.email = email
                this.passwordHash = password.hash
                this.passwordSalt = password.salt
            }.id.value.toString()
        }
    }

    override fun findUserByEmail(email: String): User? {
        return transaction(db) {
            val user = ExposedUser.find { UserTable.email eq email }.singleOrNull()

            user?.let { dbUser ->
                User(
                    dbUser.id.value.toString(),
                    dbUser.email,
                    HashedPassword(dbUser.passwordHash, dbUser.passwordSalt),
                    dbUser.address?.let {
                        UserAddress(
                            it.street,
                            it.houseNumber,
                            it.additionalAddressRow,
                            it.city,
                            it.postalCode,
                            it.countryCode
                        )
                    })
            }
        }
    }

}