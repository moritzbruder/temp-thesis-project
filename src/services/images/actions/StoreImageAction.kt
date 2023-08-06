package de.moritzbruder.services.images.actions

import de.moritzbruder.services.images.model.ImageReference
import de.moritzbruder.services.images.persistence.CombinedImageStorage
import de.moritzbruder.services.images.utilities.resize
import de.moritzbruder.services.images.utilities.toBase64
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.util.*
import javax.imageio.ImageIO

/**
 * Takes the image data, converts it to an image file, generates a low-res thumbnail of it and stores it and its
 * metadata
 */
class StoreImageAction(private val imageStorage: CombinedImageStorage) {

    operator fun invoke(base64Input: String): ImageReference {
        val bufferedImage: BufferedImage
        try {
            val imageBytes: ByteArray = Base64.getDecoder().decode(base64Input)
            bufferedImage = ImageIO.read(ByteArrayInputStream(imageBytes))
        } catch (e: Exception) {
            throw InvalidImageDataError()
        }

        // Create down-scaled version so that the larger dimension equals 24 pixels and convert it to
        val thumbnail = bufferedImage.resize(24)
        val thumbnailBase64 = thumbnail.toBase64()

        val newImageId = UUID.randomUUID()
        val img = imageStorage.storeImage(newImageId, bufferedImage, thumbnailBase64)

        return img.getReference()
    }
}

class InvalidImageDataError : Error("invalid image data")