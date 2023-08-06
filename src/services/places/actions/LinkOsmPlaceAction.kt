package de.moritzbruder.services.places.actions

import de.moritzbruder.services.places.model.PlaceOsmLink
import de.moritzbruder.services.places.persistence.PlaceStorage
import de.westnordost.osmapi.OsmConnection
import de.westnordost.osmapi.map.MapDataDao
import de.westnordost.osmapi.map.data.Element
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import org.geojson.FeatureCollection

data class LinkOsmPlaceInput(
    val osmId: Long,
    val osmType: Char,
    val userId: String,
    val placeId: String
)

/**
 * Uses osm and photon APIs to retrieve information required to create an [PlaceOsmLink] based on photon's osm-id and -type identifier combo.
 */
class LinkOsmPlaceAction(osmClient: OsmConnection, private val storage: PlaceStorage) {

    //private val osmClient = OsmConnection("https://api.openstreetmap.org/api/0.6/", "activities", null)
    private val mapDataDao = MapDataDao(osmClient)

    private val httpClient = HttpClient(Apache) {
        install(JsonFeature) {
            serializer = JacksonSerializer()
        }
    }

    suspend operator fun invoke(input: LinkOsmPlaceInput): PlaceOsmLink {
        val nodeId = when (input.osmType) {
            'N' -> input.osmId
            'W' -> mapDataDao.getWay(input.osmId).nodeIds.first()
                ?: throw InformationRetrievalException("No nodes in way.")

            'R' -> mapDataDao.getRelation(input.osmId).members.first { it.type == Element.Type.NODE }?.ref
                ?: throw InformationRetrievalException("No nodes in relation.")

            else -> throw UnknownOsmTypeException(input.osmType)
        }

        val osmNode = mapDataDao.getNode(nodeId)

        val latitude = osmNode.position.latitude
        val longitude = osmNode.position.longitude

        val featuresUrl = "https://photon.komoot.io/reverse?lon=$longitude&lat=$latitude"
        val features =
            httpClient.get<FeatureCollection>(featuresUrl) {
                headers {
                    append("Accept", "application/json")
                }
            }

        val cityName = (features.features[0]?.properties?.get("city") as? String)
            ?: (features.features[0]?.properties?.get("district") as? String)
            ?: throw InformationRetrievalException(
                "Could not reverse geocode the area of the given osm entity." +
                        "Neither 'city' not 'district' attribute are set."
            )

        val link = PlaceOsmLink(input.osmId, input.osmType, latitude, longitude, cityName)

        storage.assignPlaceWithOsmEntity(input.placeId, link)

        return link

    }

}

/**
 * Thrown if the external APIs fail to provide required information.
 */
class InformationRetrievalException(detailReason: String) :
    Exception("Could not retrieve required information: $detailReason")

/**
 * Thrown if the given osm type is not one of the expected options.
 */
class UnknownOsmTypeException(osmType: Char) : Exception("Unknown osm type: $osmType")