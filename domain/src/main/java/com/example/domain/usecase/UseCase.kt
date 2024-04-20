package com.example.domain

import kotlinx.coroutines.flow.Flow

interface UseCase<in P, out R> {
    suspend fun invoke(param: P): Flow<R>
}
