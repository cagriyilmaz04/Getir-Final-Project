package com.example.domain.usecase

import com.example.data.models.ProductEntity
import com.example.data.repository.LocalProductRepository
import com.example.domain.UseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RemoveProductUseCase @Inject constructor(
    private val repository: LocalProductRepository
) : UseCase<ProductEntity, Unit> {
    override suspend fun invoke(param: ProductEntity): Flow<Unit> = flow {
        repository.deleteProduct(param.productId)
        emit(Unit)  // Flow içerisine Unit emit ediyoruz.
    }
}