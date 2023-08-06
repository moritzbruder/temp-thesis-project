package de.moritzbruder.services.images.persistence

import de.moritzbruder.services.images.model.Image
import java.util.*

/**
 * Stores metadata for images stored using [ImageStorage].
 */
interface ImageMetaStorage {

    fun storeImageMetadata(id: UUID, width: Int, height: Int, thumbnailData: String): Image

    fun removeImageMetadata(id: UUID)

}