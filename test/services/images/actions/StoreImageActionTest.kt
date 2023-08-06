package services.images.actions

import TestData
import de.moritzbruder.services.images.actions.InvalidImageDataError
import de.moritzbruder.services.images.actions.StoreImageAction
import de.moritzbruder.services.images.model.Image
import de.moritzbruder.services.images.persistence.CombinedImageStorage
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class StoreImageActionTest {

    private val imageStorage = mockk<CombinedImageStorage>()

    @Test
    fun storesImage() {
        val id = UUID.randomUUID()
        val image = Image(id, 12, 12, "thumb")
        every { imageStorage.storeImage(any(), any(), any()) } returns image

        val action = StoreImageAction(imageStorage)
        val res = action(TestData.base64Image)

        assertEquals(image.getReference(), res)
        verify { imageStorage.storeImage(any(), any(), any()) }
    }

    @Test(expected = InvalidImageDataError::class)
    fun invalidImageFails() {
        val id = UUID.randomUUID()
        val image = Image(id, 12, 12, "thumb")
        every { imageStorage.storeImage(any(), any(), any()) } returns image
        val action = StoreImageAction(imageStorage)
        action("not an image")
    }

}