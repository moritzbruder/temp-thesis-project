package services.images.actions

import de.moritzbruder.services.images.actions.GetImageAction
import de.moritzbruder.services.images.persistence.CombinedImageStorage
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class GetImageActionTest {

    private val imageStorage = mockk<CombinedImageStorage>()

    @Test
    fun storesImage() {
        val id = UUID.randomUUID()
        val array = ByteArray(13)
        every { imageStorage.getImage(id) } returns array
        val action = GetImageAction(imageStorage)
        val res = action(id)
        assertEquals(array, res)
        verify { imageStorage.getImage(id) }
    }

}