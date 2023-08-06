package services.auth.api

import de.moritzbruder.defaultConfig
import de.moritzbruder.services.auth.actions.CreateAccountAction
import de.moritzbruder.services.auth.actions.InvalidCredentialsError
import de.moritzbruder.services.auth.actions.LoginAction
import de.moritzbruder.services.auth.actions.UserInput
import de.moritzbruder.services.auth.api.authRoutes
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.serialization.json.json
import org.junit.Test

class ApiTest {

    private val loginAction = mockk<LoginAction>()
    private val createAccountAction = mockk<CreateAccountAction>()

    private fun <R> runApp(test: TestApplicationEngine.() -> R) {
        withTestApplication({
            defaultConfig()
            routing {
                authRoutes(loginAction, createAccountAction)
            }
        }, test)
    }


    @Test
    fun missingEmail() = runApp {
        handleRequest {
            method = HttpMethod.Get
            uri = "/login"
            addHeader("auth-pw", "pw")
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, response.status())
            assertEquals(true, response.content?.contains("email and password headers required"))
        }
    }

    @Test
    fun missingPassword() = runApp {
        handleRequest {
            method = HttpMethod.Get
            uri = "/login"
            addHeader("auth-email", "email")
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, response.status())
            assertEquals(true, response.content?.contains("email and password headers required"))
        }
    }

    @Test
    fun validLogin() = runApp {
        every { loginAction.invoke("email", "pw") } returns "token"
        handleRequest {
            method = HttpMethod.Get
            uri = "/login"
            addHeader("auth-email", "email")
            addHeader("auth-pw", "pw")
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("token", response.content)
        }
    }

    @Test
    fun invalidLogin() = runApp {
        every { loginAction.invoke("email", "pw") } throws InvalidCredentialsError()
        handleRequest {
            method = HttpMethod.Get
            uri = "/login"
            addHeader("auth-email", "email")
            addHeader("auth-pw", "pw")
        }.apply {
            assertEquals(HttpStatusCode.Unauthorized, response.status())
        }
    }

    @Test
    fun requestWithoutBody() = runApp {
        handleRequest {
            method = HttpMethod.Post
            uri = "/user"
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, response.status())
        }
    }

    @Test
    fun requestWithPartialBody() = runApp {
        handleRequest {
            method = HttpMethod.Post
            uri = "/user"
            setBody(json {
                "email" to "test@mail.com"
                "password" to "password"
            }.toString())
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, response.status())
        }
    }

    @Test
    fun invalidEmail() = runApp {
        val body = json {
            "email" to "not an email"
            "password" to "password"
            "firstName" to "first"
            "lastName" to "last"
        }.toString()
        handleRequest {
            method = HttpMethod.Post
            uri = "/user"
            setBody(body)
            addHeader("Content-Type", "application/json")
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, response.status())
            assertEquals(true, response.content?.contains("not a valid email address"))
        }
    }

    @Test
    fun invalidPassword() = runApp {
        val body = json {
            "email" to "test@mail.com"
            "password" to "pw"
            "firstName" to "first"
            "lastName" to "last"
        }.toString()
        handleRequest {
            method = HttpMethod.Post
            uri = "/user"
            setBody(body)
            addHeader("Content-Type", "application/json")
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, response.status())
            assertEquals(true, response.content?.contains("password is too short"))
        }
    }

    @Test
    fun validAccountCreation() = runApp {
        every { createAccountAction.invoke(UserInput("test@mail.com", "first", "last", "password")) } returns "user-id"
        val body = json {
            "email" to "test@mail.com"
            "password" to "password"
            "firstName" to "first"
            "lastName" to "last"
        }.toString()
        handleRequest {
            method = HttpMethod.Post
            uri = "/user"
            setBody(body)
            addHeader("Content-Type", "application/json")
        }.apply {
            assertEquals(HttpStatusCode.Created, response.status())
        }
        verify { createAccountAction.invoke(UserInput("test@mail.com", "first", "last", "password")) }
    }

}