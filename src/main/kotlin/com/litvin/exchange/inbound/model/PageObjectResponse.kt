package com.litvin.exchange.inbound.model

interface PageObjectResponse<T, R> {
    val items: List<T>
    val nextPageToken: R?
}
