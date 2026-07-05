package com.andreich.news.domain.model

sealed interface RequestResult {
    data object Success : RequestResult

    sealed interface Failure : RequestResult {
        data class NoInternet(val message: String) : Failure
        data class Timeout(val message: String) : Failure
        data class Unauthorized(val message: String) : Failure
        data class Server(val message: String) : Failure
        data class Unknown(val message: String) : Failure
        data class Serialization(val message: String) : Failure
    }
}