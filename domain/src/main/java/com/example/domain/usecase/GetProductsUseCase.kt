package com.example.domain


import com.example.data.models.BeveragePack
import com.example.data.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) : UseCase<Unit, List<BeveragePack>> {

    override fun invoke(params: Unit): Flow<List<BeveragePack>> {
        return productRepository.getProducts()
    }
}