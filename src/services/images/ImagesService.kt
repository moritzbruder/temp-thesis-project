package de.moritzbruder.services.images

import de.moritzbruder.services.Service
import de.moritzbruder.services.images.actions.GetImageAction
import de.moritzbruder.services.images.actions.StoreImageAction
import de.moritzbruder.services.images.api.imageRoutes
import de.moritzbruder.services.images.model.ImageReference
import de.moritzbruder.services.images.persistence.CombinedImageStorage
import de.moritzbruder.services.images.persistence.implementation.FileImageStorage
import de.moritzbruder.services.images.persistence.implementation.exposed.ExposedImageMetaStorage
import io.ktor.routing.*
import org.jetbrains.exposed.sql.Database
import java.io.File
import java.util.*
import java.util.concurrent.Future
import java.util.concurrent.FutureTask

/**
 * Monolithic implementation of [ImagesServiceInterface]
 */

class ImagesService(db: Database, imagesDir: File) : Service, ImagesServiceInterface {
    private val storage = CombinedImageStorage(
        FileImageStorage(imagesDir),
        ExposedImageMetaStorage(db)
    )
    private val storeImageAction = StoreImageAction(storage)
    override fun storeImage(base64encoded: String): Future<ImageReference> {
        return FutureTask {
            storeImageAction(base64encoded)
        }.apply { run() }
    }

    override fun deleteImage(id: String): Future<Unit> {
        return FutureTask {
            storage.deleteImage(UUID.fromString(id))
        }.apply { run() }
    }

    override fun routes(): Routing.() -> Unit = {
        imageRoutes(GetImageAction(storage))
    }
}