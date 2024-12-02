package org.company.app.upbit_client

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readBytes
import io.ktor.websocket.send
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.company.app.upbit_client.dto.ResponseOrderBook
import org.company.app.upbit_client.dto.ResponseTrade


class UpbitClient {
    private var subscribeJob: Job? = null

    private val _orderBookResponses = MutableSharedFlow<ResponseOrderBook>()
    val orderBookResponses: SharedFlow<ResponseOrderBook> = _orderBookResponses

    private val _tradeResponses = MutableSharedFlow<ResponseTrade>()
    val tradeResponses: SharedFlow<ResponseTrade> = _tradeResponses


    private var orderBookSession: DefaultClientWebSocketSession? = null
    private var client: HttpClient = createHttpClient()

    private fun createHttpClient() = HttpClient {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }

        install(ContentNegotiation) {
            json(Json {
                explicitNulls = false
                prettyPrint = true
                isLenient = true
            })
        }
    }

    // code ex: KRW-DOGE
    suspend fun subscribe(code: String) {
        subscribeJob?.cancel()
        orderBookSession?.close()

        orderBookSession = client.webSocketSession(UPBIT_WEBSOCKET_PUBLIC_URL).also { session ->
            val request = getRequestJsonString(code)
            session.send(content = request)

            subscribeJob = GlobalScope.launch {
                for (frame in session.incoming) {
                    when (frame) {
                        is Frame.Binary -> {
                            val text = frame.readBytes().decodeToString()
                            val json = Json.parseToJsonElement(text)

                            if (json.jsonObject["type"]?.jsonPrimitive?.content == "orderbook") {
                                val orderBook = Json.decodeFromString<ResponseOrderBook>(text)
                                println("[LOG]: orderBook 메시지: $orderBook")
                                _orderBookResponses.emit(orderBook)
                            } else if (json.jsonObject["type"]?.jsonPrimitive?.content == "trade") {
                                val trade = Json.decodeFromString<ResponseTrade>(text)
                                println("[LOG]: trade 메시지: $trade")
                                _tradeResponses.emit(trade)
                            }
                        }

                        else -> println("[LOG]: 기타 메세지 프레임 수신")
                    }
                }
            }
        }
    }

    suspend fun unsubscribe() {
        subscribeJob?.cancel()
        subscribeJob = null
        orderBookSession?.close()
        orderBookSession = null
    }

    fun close() {
        subscribeJob?.cancel()
        subscribeJob = null
        orderBookSession = null
        client.close()
    }

    private fun getRequestJsonString(code: String): String {
        return "[{\"ticket\":\"test-user\"},{\"type\":\"trade\",\"codes\":[\"${code}\"]},{\"type\":\"orderbook\",\"codes\":[\"${code}\"]}]"
    }

    companion object {
        private const val UPBIT_WEBSOCKET_PUBLIC_URL = "wss://api.upbit.com/websocket/v1"
    }
}