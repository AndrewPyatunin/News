package com.andreich.news.network

import com.andreich.news.domain.model.RequestResult
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

suspend inline fun <T> safeApiCall(
    crossinline apiCall: suspend () -> T,
    crossinline onSuccess: suspend (T) -> Unit
): RequestResult {
    return try {
        val data = apiCall()
        onSuccess(data)
        RequestResult.Success
    } catch (e: HttpRequestTimeoutException) {
        RequestResult.Failure.Timeout(e.message.orEmpty())
    } catch (e: ConnectTimeoutException) {
        RequestResult.Failure.Timeout(e.message.orEmpty())
    } catch (e: ClientRequestException) {
        when (e.response.status) {
            HttpStatusCode.Unauthorized -> {
                RequestResult.Failure.Unauthorized(e.message)
            }

            else -> {
                RequestResult.Failure.Unknown(e.message)
            }
        }

    } catch (e: ServerResponseException) {
        RequestResult.Failure.Server(e.message)
    } catch (e: SerializationException) {
        RequestResult.Failure.Serialization(e.message.orEmpty())
    } catch (e: IOException) {
        RequestResult.Failure.NoInternet(e.message.orEmpty())
    } catch (e: Exception) {
        RequestResult.Failure.Unknown(e.message.orEmpty())
    }
}
