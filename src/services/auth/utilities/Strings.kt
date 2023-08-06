package de.moritzbruder.services.auth.utilities

import org.apache.commons.validator.routines.EmailValidator

/**
 * Returns true if string is well-formed email address as determined by apache commons.
 */
fun String.isEmailAddress(): Boolean {
    return EmailValidator.getInstance().isValid(this)
}