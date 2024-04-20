package com.example.domain.usecase

import com.example.data.models.ProductEntity
import com.example.data.repository.LocalProductRepository
import com.example.domain.UseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocalProductsUseCase @Inject constructor(
    private val localProductRepository: LocalProductRepository
) : UseCase<Unit, List<ProductEntity>> {
    override suspend fun invoke(params: Unit): Flow<List<ProductEntity>> {
        return localProductRepository.getAllProducts()
    }
}