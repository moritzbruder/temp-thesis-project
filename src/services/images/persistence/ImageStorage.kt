package de.moritzbruder.services.images.persistence

import java.awt.image.BufferedImage
import java.util.*

/**
 * Stores images under UUIDs
 */
interface ImageStorage {

    /**
     * Stores the image
     */
    fun storeImage(id: UUID, image: BufferedImage)

    /**
     * Returns data containing the image with the given [id].
     */
    fun getImage(id: UUID): ByteArray?

    /**
     * Removes the image.´with the given [id].
     */
    fun deleteImage(id: UUID)

}