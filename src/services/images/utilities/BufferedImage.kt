package de.moritzbruder.services.images.utilities

import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.*
import javax.imageio.ImageIO

/**
 * Resizes the image so the larger dimension will match [maxDimen] the other dimension will be scaled proportionally.
 */
fun BufferedImage.resize(maxDimen: Int): BufferedImage {
    // Calculate new dimensions
    val newWidth =
        if (width >= height) maxDimen else ((maxDimen.toDouble() / height.toDouble()) * width.toDouble()).toInt()
    val newHeight =
        if (height >= width) maxDimen else ((maxDimen.toDouble() / width.toDouble()) * height.toDouble()).toInt()

    // Create new image
    val resized = BufferedImage(newWidth, newHeight, this.type)

    // Draw old image into new, scaled image
    val g = resized.createGraphics()
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
    g.drawImage(this, 0, 0, newWidth, newHeight, 0, 0, this.width, this.height, null)
    g.dispose()

    // Return result
    return resized

}

/**
 * Turns the image to a base64 png string.
 */
fun BufferedImage.toBase64(): String {
    val baos = ByteArrayOutputStream()
    ImageIO.write(this, "png", baos)
    return Base64.getEncoder().encodeToString(baos.toByteArray())

}