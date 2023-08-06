@file:Suppress("MemberVisibilityCanBePrivate")

package de.moritzbruder.services.activities.model

import de.moritzbruder.services.places.model.Place
import de.moritzbruder.services.places.model.PlaceCategory

class PublicActivity(activity: Activity, publicPlace: PublicPlace) {

    val id: String
    val title: String
    val imageId: String?
    val imageThumbnail: String?
    val description: String?
    val place: PublicPlace = publicPlace

    init {
        this.id = activity.id
        this.title = activity.title
        this.imageId = activity.imageId
        this.imageThumbnail = activity.imageThumbnail
        this.description = activity.description
    }

}

class PublicPlace(place: Place) {

    val name: String
    val pictureId: String
    val pictureThumbnail: String
    val description: String
    val category: PlaceCategory
    val osmLink: OsmLink

    init {
        this.name = place.name
        this.pictureId = place.pictureId
        this.pictureThumbnail = place.pictureThumbnail
        this.description = place.description
        this.category = place.category
        this.osmLink = OsmLink(place.osmLink!!.osmType, place.osmLink.osmId)
    }
}

data class OsmLink(val osmType: Char, val osmId: Long)