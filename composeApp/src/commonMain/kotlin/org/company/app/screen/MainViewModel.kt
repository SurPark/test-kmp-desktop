package org.company.app.screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.company.app.AppNavigation
import org.company.app.upbit_client.UpbitClient
import org.company.app.upbit_client.dto.ResponseOrderBook
import org.company.app.upbit_client.dto.ResponseTrade
import org.company.app.utils.Queue

class MainViewModel {
    private val upbitClient = UpbitClient()

    private val _currentPrice = mutableStateOf<Double?>(null)
    val currentPrice: State<Double?> = _currentPrice

    private val _orderBook = mutableStateOf<ResponseOrderBook?>(null)
    val orderBook: State<ResponseOrderBook?> = _orderBook

    private val queueTrades = Queue<ResponseTrade>()
    private val _trades = mutableStateOf<List<ResponseTrade>>(emptyList())
    val trades: State<List<ResponseTrade>> = _trades

    private val _navigation = mutableStateOf<AppNavigation?>(null)
    val navigation: State<AppNavigation?> = _navigation

    val inputTicker = mutableStateOf("KRW-DOGE")

    private var subscribeScope:CoroutineScope? = null

    init {
        subscribe()
    }

    fun onSearchTicker() {
        GlobalScope.launch {
            upbitClient.unsubscribe()

            withContext(Dispatchers.Main) {
                queueTrades.clear()
                _trades.value = emptyList()
                _orderBook.value = null
                _currentPrice.value = null
            }

            subscribe()
        }
    }

    private fun subscribe() {
        subscribeScope?.cancel()
        subscribeScope = CoroutineScope(Dispatchers.Default)

        subscribeScope?.launch {
            upbitClient.subscribe(inputTicker.value)

            launch {
                upbitClient.tradeResponses.collect { tradeResponse ->
                    _currentPrice.value = tradeResponse.tradePrice
                    queueTrades.add(tradeResponse)
                    if (queueTrades.count() > 10) {
                        queueTrades.remove()
                    }
                    _trades.value = queueTrades.items.toList()
                }
            }

            launch {
                upbitClient.orderBookResponses.collect { orderBookResponse ->
                    _orderBook.value = orderBookResponse.copy()
                }
            }

        }
    }

    fun logout() {
        upbitClient.close()
        _navigation.value = AppNavigation.Login
    }

}