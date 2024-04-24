package com.example.getirmultideneme

import com.example.data.models.ProductEntity
import com.example.data.repository.LocalProductRepository
import com.example.presentation.base.util.Resource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class SharedViewModelTest {

    private lateinit var viewModel: SharedViewModel
    private val localRepository: LocalProductRepository = mockk(relaxed = true)
    val sampleProduct = ProductEntity(
        id = 511,
        productId = "5d6d2c696deb8b00011f7665",
        name = "Kuzeyden",
        attribute = "2 x 5 L",
        thumbnailURL = "http://cdn.getir.com/product/5d6d2c696deb8b00011f7665_tr_1617795578982.jpeg",
        price = 59.2,
        imageURL = "http://cdn.getir.com/product/5d6d2c696deb8b00011f7665_tr_1617795578982.jpeg",
        description = null,
        quantity = 1
    )
    @Before
    fun setup() {
        // Mock the local repository to return the sample product when queried by ID
        coEvery { localRepository.getProductById("5d6d2c696deb8b00011f7665") } returns flowOf(sampleProduct)
        viewModel = SharedViewModel(localRepository)
    }

    @Test
    fun `add product to cart adds and retrieves products correctly`() = runBlockingTest {
        // Mock the addProductToCart to simulate adding the product
        coEvery { localRepository.addProductToCart(sampleProduct) } returns Unit
        viewModel.addProductToCart(sampleProduct)

        // Check if the product addition triggers a list update
        viewModel.products.collect { result ->
            assertEquals(Resource.Success(listOf(sampleProduct)), result)
        }
    }

    @Test
    fun `update product quantity updates correctly`() = runBlockingTest {
        // Assume the product is already in the repository
        coEvery { localRepository.updateProduct(any()) } returns Unit
        viewModel.updateQuantity(sampleProduct, increase = true)

        // Verify the update method was called
        coVerify { localRepository.updateProduct(any()) }
    }

    @Test
    fun `delete product from cart removes correctly`() = runBlockingTest {
        // Assume the product to delete is found
        coEvery { localRepository.deleteProduct("5d6d2c696deb8b00011f7665") } returns Unit
        viewModel.deleteProductFromCart(sampleProduct)

        // Verify the delete method was called
        coVerify { localRepository.deleteProduct(sampleProduct.productId) }
    }
}
