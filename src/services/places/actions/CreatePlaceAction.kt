package de.moritzbruder.services.places.actions

import de.moritzbruder.services.images.ImagesService
import de.moritzbruder.services.places.model.Place
import de.moritzbruder.services.places.model.PlaceCategory
import de.moritzbruder.services.places.persistence.PlaceStorage

data class PlaceInput(
    val name: String,
    val description: String,
    val category: String,
    val pictureBase64: String
)

class CreatePlaceAction(
    private val storage: PlaceStorage,
    private val imageService: ImagesService
) {

    operator fun invoke(input: PlaceInput, userId: String): Place {
        val category = PlaceCategory.valueOf(input.category)

        // Store image
        val pictureReference = imageService.storeImage(input.pictureBase64).get()

        // Create new Place instance in database
        val newPlace = storage.createPlace(
            userId,
            input.name,
            input.description,
            category,
            pictureReference.imageId,
            pictureReference.thumbnailBase64
        )

        return newPlace
    }

}