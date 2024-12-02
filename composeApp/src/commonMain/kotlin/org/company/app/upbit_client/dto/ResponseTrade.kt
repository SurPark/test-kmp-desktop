package org.company.app.upbit_client.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseTrade(
    @SerialName("ask_bid")
    val askBid: String,
    @SerialName("best_ask_price")
    val bestAskPrice: Double,
    @SerialName("best_ask_size")
    val bestAskSize: Double,
    @SerialName("best_bid_price")
    val bestBidPrice: Double,
    @SerialName("best_bid_size")
    val bestBidSize: Double,
    @SerialName("change")
    val change: String,
    @SerialName("change_price")
    val changePrice: Double,
    @SerialName("code")
    val code: String,
    @SerialName("prev_closing_price")
    val prevClosingPrice: Double,
    @SerialName("sequential_id")
    val sequentialId: Long,
    @SerialName("stream_type")
    val streamType: String,
    @SerialName("timestamp")
    val timestamp: Long,
    @SerialName("trade_date")
    val tradeDate: String,
    @SerialName("trade_price")
    val tradePrice: Double,
    @SerialName("trade_time")
    val tradeTime: String,
    @SerialName("trade_timestamp")
    val tradeTimestamp: Long,
    @SerialName("trade_volume")
    val tradeVolume: Double,
    @SerialName("type")
    val type: String
)