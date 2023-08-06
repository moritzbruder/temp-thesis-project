package de.moritzbruder.services.images.persistence

import de.moritzbruder.services.images.model.Image
import java.awt.image.BufferedImage
import java.util.*

/**
 * Combines a [ImageStorage] and a [ImageMetaStorage] to ensure consistency.
 */
class CombinedImageStorage(private val imageStorage: ImageStorage, private val imageMetaStorage: ImageMetaStorage) {

    /**
     * Stores image and its metadata.
     */
    fun storeImage(id: UUID, image: BufferedImage, thumbnailBase64: String): Image {
        imageStorage.storeImage(id, image)
        return imageMetaStorage.storeImageMetadata(id, image.width, image.height, thumbnailBase64)

    }

    /**
     * Removes an image and its metadata
     */
    fun deleteImage(id: UUID) {
        imageStorage.deleteImage(id)
        imageMetaStorage.removeImageMetadata(id)

    }

    /**
     * Returns the full image with [id] as a [ByteArray].
     */
    fun getImage(id: UUID): ByteArray? {
        return imageStorage.getImage(id)

    }

}