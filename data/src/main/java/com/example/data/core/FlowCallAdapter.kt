package data.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FlowCallAdapter<R>(private val responseType: Type) : CallAdapter<R, Flow<R>> {
    override fun responseType(): Type = responseType

    override fun adapt(call: Call<R>): Flow<R> = flow {
        emit(suspendCoroutine { continuation ->
            call.enqueue(object : Callback<R> {
                override fun onResponse(call: Call<R>, response: Response<R>) {
                    if (response.isSuccessful) {
                        response.body()?.let { body ->
                            continuation.resume(body)
                        } ?: continuation.resumeWithException(
                            NullPointerException("Response body is null")
                        )
                    } else {
                        continuation.resumeWithException(
                            RuntimeException("Failed call: ${response.code()} ${response.message()}")
                        )
                    }
                }

                override fun onFailure(call: Call<R>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        })
    }
}
