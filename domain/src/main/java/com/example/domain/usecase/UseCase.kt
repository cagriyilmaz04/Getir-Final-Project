package com.example.domain

import kotlinx.coroutines.flow.Flow

interface UseCase<in Parameter, out Result> {
    operator fun invoke(param: Parameter): Flow<Result>
}
