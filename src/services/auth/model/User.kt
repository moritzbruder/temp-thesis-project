package de.moritzbruder.services.auth.model

import de.moritzbruder.services.auth.utilities.HashedPassword

/**
 * A user of the system.
 */
class User(

    /**
     * UUID of the user.
     */
    val userId: String,

    /**
     * E-mail address of the user.
     */
    val email: String,

    /**
     * Password of the user (combination of hash and salt)
     */
    val password: HashedPassword,

    /**
     * Optional address of the user for invoicing.
     */
    val address: UserAddress?
)

/**
 * Street address for a user.
 */
class UserAddress(
    var street: String,
    var houseNumber: String,
    var additionalAddressRow: String?,
    var city: String,
    var postalCode: String,
    var countryCode: String
)