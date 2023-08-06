package de.moritzbruder.services.images.persistence.implementation

import de.moritzbruder.services.images.persistence.ImageStorage
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import javax.imageio.ImageIO

/**
 * File-based implementation of [ImageStorage]
 * All image files are stored in the given [storageDir].
 */
class FileImageStorage(val storageDir: File) : ImageStorage {

    init {
        println("Creating file image directory if needed: ${storageDir.absolutePath}")
        if (!storageDir.exists()) {
            storageDir.mkdirs()
            storageDir.mkdir()
            println("Did create storage directory for image storage")

        }
    }

    override fun storeImage(id: UUID, image: BufferedImage) {
        val imgFile = File(storageDir, id.toString())
        imgFile.createNewFile()
        ImageIO.write(image, "png", imgFile)

    }

    override fun getImage(id: UUID): ByteArray? {
        val imgFile = File(storageDir, id.toString())
        if (!imgFile.exists()) {
            return null
        }
        val image = ImageIO.read(imgFile)
        val baos = ByteArrayOutputStream()
        ImageIO.write(image, "png", baos)

        return baos.toByteArray()

    }

    override fun deleteImage(id: UUID) {
        val imgFile = File(storageDir, id.toString())
        imgFile.delete()

    }

}