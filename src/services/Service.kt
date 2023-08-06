package de.moritzbruder.services

import io.ktor.routing.*

interface Service {

    fun routes(): Routing.() -> Unit

}

class ServiceRef<T : Service> {
    lateinit var service: T
}

fun Routing.install(ref: ServiceRef<out Service>) {
    ref.service.routes().invoke(this)
}