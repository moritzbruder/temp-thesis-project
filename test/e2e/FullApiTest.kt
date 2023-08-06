package e2e

import TestData
import createTestDb
import de.moritzbruder.runApp
import de.moritzbruder.services.places.model.PlaceCategory
import de.westnordost.osmapi.OsmConnection
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.content
import kotlinx.serialization.json.json
import kotlinx.serialization.json.jsonArray
import org.junit.Test
import java.io.File

class FullApiTest {

    private fun <R> runApp(test: TestApplicationEngine.() -> R) {
        val osmClient = mockk<OsmConnection>()
        val imgDir = File("./tmp/images")
        imgDir.mkdirs()
        withTestApplication({
            runApp(createTestDb(), osmClient, imgDir)
        }, test)
        imgDir.deleteRecursively()
    }

    @OptIn(UnstableDefault::class)
    @Test
    fun servicesWorkDeployedTogether() = runApp {
        // create account
        handleRequest {
            method = HttpMethod.Post
            uri = "/user"
            setBody(json {
                "email" to "test@email.com"
                "password" to "password123"
                "firstName" to "John"
                "lastName" to "Doe"
            }.toString())
            addHeader("Content-Type", "application/json")
        }.apply {
            assertEquals(HttpStatusCode.Created, response.status())
        }

        // log in
        val token: String
        handleRequest {
            method = HttpMethod.Get
            uri = "/login"
            addHeader("auth-email", "test@email.com")
            addHeader("auth-pw", "password123")
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
            token = response.content!!
        }

        // create place
        val placeId: String
        handleRequest {
            method = HttpMethod.Post
            uri = "/place"
            addHeader("Authorization", "Bearer $token")
            setBody(json {
                "name" to "Test Place"
                "description" to "this is my place"
                "category" to PlaceCategory.OutdoorsArea.name
                "pictureBase64" to TestData.base64Image
            }.toString())
            addHeader("Content-Type", "application/json")
        }.apply {
            assertEquals(HttpStatusCode.Created, response.status())
            placeId = Json.parseJson(response.content!!).jsonObject["id"]!!.content
        }


        // create activity
        handleRequest {
            method = HttpMethod.Post
            uri = "/activity"
            addHeader("Authorization", "Bearer $token")
            setBody(json {
                "placeId" to placeId
                "title" to "this is my activity"
                "description" to "test"
                "timeSuggestions" to jsonArray {
                    json {
                        "days" to jsonArray {
                            +1 + 2
                        }
                        "minuteOfDay" to 1170
                    }
                }
            }.toString())
            addHeader("Content-Type", "application/json")
        }.apply {
            assertEquals(HttpStatusCode.Created, response.status())
            println(response.content!!)
        }

        // get image for place
        val imageId: String
        handleRequest {
            method = HttpMethod.Get
            uri = "/place/$placeId"
            addHeader("Authorization", "Bearer $token")
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
            imageId = Json.parseJson(response.content!!).jsonObject["pictureId"]!!.content
        }

        handleRequest {
            method = HttpMethod.Get
            uri = "/image/$imageId"
            addHeader("Authorization", "Bearer $token")
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
            assert((response.byteContent?.size ?: 0) > 0)
        }
    }

}