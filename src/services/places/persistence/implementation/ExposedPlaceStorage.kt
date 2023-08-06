package de.moritzbruder.services.places.persistence.implementation

import de.moritzbruder.services.places.model.CoordinateBox
import de.moritzbruder.services.places.model.Place
import de.moritzbruder.services.places.model.PlaceCategory
import de.moritzbruder.services.places.model.PlaceOsmLink
import de.moritzbruder.services.places.persistence.PlaceStorage
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

/**
 * SQL-based implementation of [PlaceStorage]
 */
class ExposedPlaceStorage(private val db: Database) : PlaceStorage {

    init {
        transaction(db) {
            SchemaUtils.createMissingTablesAndColumns(PlacesTable, PlaceOsmLinksTable)
        }
    }

    override fun createPlace(
        owner: String,
        name: String,
        description: String,
        category: PlaceCategory,
        pictureId: String,
        pictureThumbnail: String
    ): Place {
        val place = transaction(db) {
            ExposedPlace.new {
                this.owner = owner
                this.name = name
                this.pictureId = pictureId
                this.pictureThumbnail = pictureThumbnail
                this.description = description
                this.category = category
            }
        }

        return Place(place.id.value.toString(), owner, name, pictureId, pictureThumbnail, description, category, null)

    }

    override fun assignPlaceWithOsmEntity(placeId: String, osmLink: PlaceOsmLink) {
        try {
            transaction(db) {
                val place = ExposedPlace.findById(UUID.fromString(placeId))
                    ?: throw RuntimeException("Place with id $placeId not found.")
                ExposedOsmLink.new(osmLink.osmId) {
                    this.place = place
                    this.latitude = osmLink.latitude
                    this.longitude = osmLink.longitude
                    this.osmType = osmLink.osmType.toString()
                    this.cityName = osmLink.cityName
                }
            }
        } catch (e: ExposedSQLException) {
            // Unique key constraint violated
            if (e.sqlState == "23505") {
                throw ConflictError("osm entity already linked")
            } else {
                throw e
            }
        }
    }

    override fun findPlaceById(placeId: String): Place? {
        return transaction(db) {
            ExposedPlace.findById(UUID.fromString(placeId))?.toPlace()
        }
    }

    override fun findPlacesByOwner(ownerId: String): List<Place> {
        return transaction(db) {
            ExposedPlace.find { PlacesTable.owner eq ownerId }.map {
                it.toPlace()

            }
        }
    }

    override fun findPlacesInCoordinateBox(coordinateBox: CoordinateBox): List<Place> {
        return transaction(db) {
            ExposedPlace.wrapRows((PlacesTable innerJoin PlaceOsmLinksTable).select {
                PlaceOsmLinksTable.latitude.between(coordinateBox.minLat, coordinateBox.maxLat) and
                        (PlaceOsmLinksTable.longitude.between(coordinateBox.minLon, coordinateBox.maxLon))
            }).toList().map { it.toPlace() }
        }
    }

}

/**
 * SQL Table to store [Place]s
 */
object PlacesTable : UUIDTable("places") {

    val owner = varchar("owner", 36)
    val name = text("name")
    val pictureId = varchar("picture", 36)
    val pictureThumbnail = text("picture_thumbnail")
    val description = text("description")
    val category = enumerationByName("category", 100, PlaceCategory::class)

}

/**
 * SQL Table to store [Place.osmLink]
 */
object PlaceOsmLinksTable : LongIdTable("place_osm", columnName = "osmId") {

    val place = reference("place", PlacesTable, onDelete = ReferenceOption.CASCADE)
    val osmType = char("osm_type", 1)
    val latitude = double("latitude")
    val longitude = double("longitude")
    val cityName = varchar("city_name", 100)

}

class ExposedPlace(id: EntityID<UUID>) : UUIDEntity(id) {

    companion object : UUIDEntityClass<ExposedPlace>(PlacesTable)

    var owner by PlacesTable.owner
    val osmLink by ExposedOsmLink optionalBackReferencedOn (PlaceOsmLinksTable.place)
    var name by PlacesTable.name
    var pictureId by PlacesTable.pictureId
    var pictureThumbnail by PlacesTable.pictureThumbnail
    var description by PlacesTable.description
    var category by PlacesTable.category

    fun toPlace() = Place(
        this.id.toString(),
        this.owner,
        this.name,
        this.pictureId,
        this.pictureThumbnail,
        this.description,
        this.category,
        this.osmLink?.let { osmLink ->
            PlaceOsmLink(
                osmLink.id.value,
                osmLink.osmType.toCharArray().single(),
                osmLink.latitude,
                osmLink.longitude,
                osmLink.cityName
            )
        })

}

/**
 * Association of a [Place] with an OpenStreetMap entity
 */
class ExposedOsmLink(id: EntityID<Long>) : LongEntity(id) {

    companion object : LongEntityClass<ExposedOsmLink>(PlaceOsmLinksTable)

    var place by ExposedPlace referencedOn PlaceOsmLinksTable.place
    var osmType by PlaceOsmLinksTable.osmType
    var latitude by PlaceOsmLinksTable.latitude
    var longitude by PlaceOsmLinksTable.longitude
    var cityName by PlaceOsmLinksTable.cityName

}

class ConflictError(msg: String) : Error(msg)