package de.moritzbruder.services.places.model

/**
 * Represents physical location/place/amenity.
 */
class Place(

    /**
     * UUID of the place
     */
    val id: String,

    /**
     * User who created this place.
     */
    val owner: String,

    /**
     * Friendly name of the place.
     */
    val name: String,

    /**
     * Id of the picture that represents this place.
     */
    val pictureId: String,

    /**
     * Small version of the picture with [pictureId] encoded in base64
     */
    val pictureThumbnail: String,

    /**
     * Extended description of the place.
     */
    val description: String,

    /**
     * Type of the place
     */
    val category: PlaceCategory,

    /**
     * Optional association with an osm entity (which makes the place discoverable to users)
     */
    val osmLink: PlaceOsmLink?

)

/**
 * Represents the linking of a place to an openstreetmap entity
 */
class PlaceOsmLink(

    /**
     * Unique id of the openstreetmap entity
     */
    val osmId: Long,

    /**
     * Type of the osm entity (W=way, P=place and so on)
     */
    val osmType: Char,

    /**
     * Coordinate of the place's physical location
     */
    val latitude: Double,

    /**
     * Coordinate of the place's physical location
     */
    val longitude: Double,

    /**
     * Friendly name of the city this place is in
     */
    val cityName: String

)