package com.example.domain.usecase

import com.example.data.repository.LocalProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetLocalProductsUseCase(localProductRepository: LocalProductRepository): GetLocalProductsUseCase {
        return GetLocalProductsUseCase(localProductRepository)
    }
}
