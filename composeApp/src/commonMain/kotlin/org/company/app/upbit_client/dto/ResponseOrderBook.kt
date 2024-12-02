package org.company.app.upbit_client.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseOrderBook(
    @SerialName("code") val code: String,
    @SerialName("level") val level: Int,
    @SerialName("orderbook_units") val orderBookUnits: List<OrderBookUnit>,
    @SerialName("stream_type") val streamType: String,
    @SerialName("timestamp") val timestamp: Long,
    @SerialName("total_ask_size") val totalAskSize: Double,
    @SerialName("total_bid_size") val totalBidSize: Double,
    @SerialName("type") val type: String
)