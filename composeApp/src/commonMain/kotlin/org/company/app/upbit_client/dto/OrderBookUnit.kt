package org.company.app.upbit_client.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderBookUnit(
    @SerialName("ask_price") val askPrice: Double,
    @SerialName("ask_size") val askSize: Double,
    @SerialName("bid_price") val bidPrice: Double,
    @SerialName("bid_size") val bidSize: Double
)