package de.moritzbruder.services.images.actions

import de.moritzbruder.services.images.persistence.CombinedImageStorage
import java.util.*

class GetImageAction(private val imageStorage: CombinedImageStorage) {
    operator fun invoke(imageId: UUID): ByteArray? {
        return imageStorage.getImage(imageId)
    }
}