import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.name
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

private fun connect(dbName: String): Database {
    return Database.connect(
        url = "jdbc:postgresql://localhost/${dbName}",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "password"
    )
}

fun createTestDb(): Database {
    val name = "test-${UUID.randomUUID()}".replace("-", "_")
    transaction(connect("postgres")) {
        connection.autoCommit = true
        SchemaUtils.createDatabase(name)
        connection.autoCommit = false
    }
    return connect(name)
}

fun dropTestDb(db: Database) {
    val toDrop = db.name
    transaction(connect("postgres")) {
        connection.autoCommit = true
        SchemaUtils.dropDatabase(toDrop)
        connection.autoCommit = false
    }
}