package de.moritzbruder.services.images.persistence.implementation.exposed

import de.moritzbruder.services.images.model.Image
import de.moritzbruder.services.images.persistence.ImageMetaStorage
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

/**
 * SQL-based implementation [ImageMetaStorage]
 */
class ExposedImageMetaStorage(val db: Database) : ImageMetaStorage {

    init {
        transaction(db) {
            SchemaUtils.createMissingTablesAndColumns(ExposedImageTable)

        }
    }

    override fun storeImageMetadata(id: UUID, width: Int, height: Int, thumbnailData: String): Image {
        transaction(db) {
            ExposedImage.new(id) {
                this.width = width
                this.height = height
                this.thumbnail = thumbnailData
            }
        }

        return Image(id, width, height, thumbnailData)

    }

    override fun removeImageMetadata(id: UUID) {
        transaction(db) {
            ExposedImage.findById(id)?.delete()

        }
    }

}

object ExposedImageTable : UUIDTable("image") {

    val thumbnail = text("thumbnail")
    val width = integer("width")
    val height = integer("height")

}

class ExposedImage(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ExposedImage>(ExposedImageTable)

    var thumbnail by ExposedImageTable.thumbnail
    var width by ExposedImageTable.width
    var height by ExposedImageTable.height

}