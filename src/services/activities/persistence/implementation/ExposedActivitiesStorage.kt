package de.moritzbruder.services.activities.persistence.implementation

import de.moritzbruder.services.activities.model.Activity
import de.moritzbruder.services.activities.model.TimeSuggestion
import de.moritzbruder.services.activities.model.toTimeslotString
import de.moritzbruder.services.activities.persistence.ActivitiesStorage
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.jodatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.util.*

/**
 * SQL-based implementation of [ActivitiesStorage].
 */
class ExposedActivitiesStorage(private val db: Database) : ActivitiesStorage {

    init {
        transaction(db) {
            SchemaUtils.createMissingTablesAndColumns(ActivitiesTable)
        }
    }

    override fun createActivity(
        placeId: String,
        title: String,
        description: String?,
        imageId: String?,
        imageThumbnail: String?,
        timeSuggestions: List<TimeSuggestion>,
        enabled: Boolean
    ): Activity {
        return transaction(db) {
            val newActivity = ExposedActivity.new {
                this.placeId = placeId
                this.title = title
                this.description = description
                this.imageId = imageId
                this.imageThumbnail = imageThumbnail
                this.timeslots = timeSuggestions.toTimeslotString()
                this.enabled = enabled
            }

            newActivity.toActivity()

        }
    }

    override fun markActivityDeleted(activityId: String) {
        transaction(db) {
            ExposedActivity.findById(UUID.fromString(activityId))?.deletedAt = DateTime.now()
        }
    }

    override fun getActivity(activityId: String, allowDeleted: Boolean): Activity? {
        return transaction(db) {
            ExposedActivity.find {
                ActivitiesTable.id eq UUID.fromString(activityId) and
                        (booleanParam(allowDeleted) or (ActivitiesTable.deletedAt.isNull()))
            }
            ExposedActivity.findById(UUID.fromString(activityId))?.toActivity()
        }
    }

    override fun getActivitiesForPlaces(placeIds: List<String>, filterEnabled: Boolean): List<Activity> {
        return transaction(db) {
            ExposedActivity.find {
                ActivitiesTable.placeId inList placeIds and (ActivitiesTable.deletedAt.isNull()) and
                        (booleanParam(!filterEnabled) or ActivitiesTable.enabled)
            }.map { it.toActivity() }
        }
    }

    override fun setActivityEnabled(activityId: String, enabled: Boolean) {
        return transaction(db) {
            ExposedActivity.findById(UUID.fromString(activityId))?.enabled = enabled
        }
    }

}

/**
 * Table storing [Activity]s
 */
object ActivitiesTable : UUIDTable("activities") {

    val placeId = varchar("place_id", 36)
    val title = text("title")
    val imageId = varchar("image_id", 36).nullable()
    val imageThumbnail = text("image_thumbnail").nullable()
    val description = text("description").nullable()
    val enabled = bool("enabled")
    val timeslots = text("timeslots")
    val deletedAt = datetime("deleted_at").nullable()

}

/**
 * A recurring activity suggested to users within the application during the applicable [timeslots] in the applicable [seasons].
 */
class ExposedActivity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ExposedActivity>(ActivitiesTable)

    var placeId by ActivitiesTable.placeId
    var title by ActivitiesTable.title
    var imageId by ActivitiesTable.imageId
    var imageThumbnail by ActivitiesTable.imageThumbnail
    var description by ActivitiesTable.description
    var enabled by ActivitiesTable.enabled
    var timeslots by ActivitiesTable.timeslots
    var deletedAt by ActivitiesTable.deletedAt

    fun toActivity(): Activity {
        return Activity(
            this.id.value.toString(),
            placeId,
            title,
            imageId,
            imageThumbnail,
            description,
            enabled,
            TimeSuggestion.listFromString(this.timeslots)
        )
    }

}