package de.moritzbruder.services.images.model

import java.util.*

/**
 * An Image with all its metadata.
 */
class Image(
    val id: UUID,
    val width: Int,
    val height: Int,
    val thumbnail: String
) {

    /**
     * Returns an [ImageReference] representing this image
     */
    fun getReference(): ImageReference {
        return ImageReference(
            this.id.toString(),
            this.thumbnail
        )
    }

}