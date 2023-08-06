package de.moritzbruder.services.images.model

/**
 * Reference to an image including [Image.id] and a super low-res version of the image usable to create a blur preview
 * while loading the full image.
 */
data class ImageReference(

    /**
     * Id of the image (used to retrieve full resoluition image and to cache by)
     */
    val imageId: String,

    /**
     * Compressed, small res version of the image passed along in JSON to show while loading the actual image-
     */
    val thumbnailBase64: String
)