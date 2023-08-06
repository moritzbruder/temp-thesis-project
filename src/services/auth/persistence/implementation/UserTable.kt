package de.moritzbruder.services.auth.persistence.implementation

import io.ktor.auth.*
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

/**
 * SQL Table to store [User]s
 */
object UserTable : UUIDTable("users") {

    val firstName = varchar("first_name", 50)
    val lastName = varchar("last_name", 50)
    val email = varchar("email", 100).index(isUnique = true)
    val passwordSalt = char("password_salt", 32)
    val passwordHash = char("password_hash", 64)
    val company = varchar("company", 100).nullable()
    val address = reference(
        "address",
        UserAddressTable
    ).nullable()

}

/**
 * SQL Table to store [UserAddress]es
 */
object UserAddressTable : UUIDTable("user_address") {

    val street = varchar("street", 100)
    val houseNumber = varchar("house_no", 10)
    val additionalAddressRow = varchar("additional", 100).nullable()
    val city = varchar("city", 100)
    val postalCode = varchar("postal_code", 7)
    val countryCode = varchar("country", 2)

}

/**
 * A user who can own and manage [Place]s as well as [Event]s
 */
class ExposedUser(id: EntityID<UUID>) : UUIDEntity(id), Principal {
    companion object : UUIDEntityClass<ExposedUser>(
        UserTable
    )

    /**
     * Legal first name of the user
     */
    var firstName by UserTable.firstName

    /**
     * Legal last name of the user
     */
    var lastName by UserTable.lastName

    /**
     * e-mail-address of the user
     */
    var email by UserTable.email

    /**
     * Salt used to hash the user's password
     */
    var passwordSalt by UserTable.passwordSalt

    /**
     * Hash of the password and the salt
     */
    var passwordHash by UserTable.passwordHash

    /**
     * Name of the user's company
     */
    var company by UserTable.company

    /**
     * Postal address of the user's company (for potential invoicing)
     */
    var address by ExposedUserAddress optionalReferencedOn UserTable.address

}

/**
 * The postal and invoice address of a [User]
 */
class ExposedUserAddress(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ExposedUserAddress>(
        UserAddressTable
    )

    var street by UserAddressTable.street
    var houseNumber by UserAddressTable.houseNumber
    var additionalAddressRow by UserAddressTable.additionalAddressRow
    var city by UserAddressTable.city
    var postalCode by UserAddressTable.postalCode
    var countryCode by UserAddressTable.countryCode

}