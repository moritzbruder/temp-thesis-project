package de.moritzbruder.services.images

import de.moritzbruder.services.images.model.ImageReference
import java.util.concurrent.Future

/**
 * Service responsible for storing and serving images.
 */
interface ImagesServiceInterface {

    /**
     * Stores the passed image and returns a reference to it.
     */
    fun storeImage(base64encoded: String): Future<ImageReference>

    /**
     * Removes the image with the given id
     */
    fun deleteImage(id: String): Future<Unit>

}