package de.moritzbruder

import com.fasterxml.jackson.datatype.joda.JodaModule
import de.moritzbruder.services.ServiceRef
import de.moritzbruder.services.activities.ActivitiesService
import de.moritzbruder.services.auth.AuthService
import de.moritzbruder.services.images.ImagesService
import de.moritzbruder.services.install
import de.moritzbruder.services.places.PlacesService
import de.moritzbruder.shared.auth.JwtAuthentication
import de.westnordost.osmapi.OsmConnection
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.Database
import java.io.File

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.defaultConfig() {
    install(CORS) {
        // Whitelist Methods
        method(HttpMethod.Options)
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)

        // Whitelist headers
        header("auth-email")
        header("auth-pw")
        header(HttpHeaders.Authorization)
        header(HttpHeaders.ContentType)

        // Whitelist hosts
        anyHost()

        allowCredentials = true

    }

    install(JwtAuthentication) {
    }

    install(ContentNegotiation) {
        jackson {
            registerModule(JodaModule())
        }
    }
}

fun Application.runApp(db: Database, osmClient: OsmConnection, imageDir: File) {
    defaultConfig()

    val authService = ServiceRef<AuthService>()
    val imagesService = ServiceRef<ImagesService>()
    val placesService = ServiceRef<PlacesService>()
    val activitiesService = ServiceRef<ActivitiesService>()

    authService.service = AuthService(db)
    imagesService.service = ImagesService(db, imageDir)
    placesService.service = PlacesService(db, imagesService, osmClient)
    activitiesService.service = ActivitiesService(db, imagesService, placesService)

    routing {
        install(authService)
        install(imagesService)
        install(placesService)
        install(activitiesService)
    }
}

/**
 * Centralized, shared Ktor configuration
 */
@Suppress("unused") // Referenced in application.conf
@JvmOverloads
fun Application.module(testing: Boolean = false) {
    val db = Database.connect(
        url = "jdbc:postgresql://localhost/postgres",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "password"
    )
    val osmClient = OsmConnection(
        "https://api.openstreetmap.org/api/0.6/",
        "activities",
        null
    )

    val imageDir = File("/var/activities/images")
    if (!imageDir.exists()) {
        imageDir.mkdirs()
    }

    runApp(db, osmClient, imageDir)
}