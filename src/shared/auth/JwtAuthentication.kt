package de.moritzbruder.shared.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.util.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import shared.util.PemUtils
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

/**
 * Configuration for Ktor'S JWT feature
 */
object JwtAuthentication : ApplicationFeature<Application, Authentication.Configuration, Authentication> {

    private const val issuer = "de.moritzbruder.activities"
    private const val validityInMs = 36_000_00 * 10 // 10 hours
    private val algorithm = run {
        val privateKey = PemUtils.readPrivateKeyFromFile("./cert/jwt/private.pem", "RSA")
        val publicKey = PemUtils.readPublicKeyFromFile("./cert/jwt/public.pem", "RSA")
        Algorithm.RSA512(publicKey as RSAPublicKey, privateKey as RSAPrivateKey)
    }

    /**
     * Verifies token based on the configuration of this JwtAuthentication
     */
    private val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    /**
     * Produce a token for the given user.
     */
    fun makeToken(userId: String): String = JWT.create()
        .withSubject(userId)
        .withIssuer(issuer)
        .withExpiresAt(DateTime.now().plusMillis(validityInMs).toDate())
        .sign(algorithm)

    /**
     * Ktor Feature override
     */
    override val key: AttributeKey<Authentication>
        get() = Authentication.key

    /**
     * Install & configure Ktor Authentication feature
     */
    override fun install(pipeline: Application, configure: Authentication.Configuration.() -> Unit): Authentication {
        return Authentication().apply {
            configure {
                jwt {
                    verifier(verifier)
                    realm = "ktor.io"
                    validate(userValidation)
                }
            }
        }
    }

    /**
     * Validates the [JWTCredential]s passed in the call and retrieves a [Principal] (in this case a [User])
     */
    private val userValidation: suspend ApplicationCall.(JWTCredential) -> Principal? = {
        it.payload.subject?.let { userId ->
            transaction {
                UserIdPrincipal(userId)

            }
        }
    }

}

/**
 * Returns the userId making the call as determined by an Authentiucation feature.
 */
val ApplicationCall.userId: String
    get() = this.authentication.principal<UserIdPrincipal>()!!.name