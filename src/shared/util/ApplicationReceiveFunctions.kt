package de.moritzbruder.shared.util

import io.ktor.application.*
import io.ktor.request.*

/**
 * Tries to receive object from call and ignores all exceptions, returning null instead.
 */
suspend inline fun <reified T : Any> ApplicationCall.tryReceive(): T? {
    return try {
        this.receiveOrNull()
    } catch (e: Exception) {
        null
    }
}