package org.company.app.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachReversed
import multiplatform_app.composeapp.generated.resources.Res
import multiplatform_app.composeapp.generated.resources.ic_dark_mode
import multiplatform_app.composeapp.generated.resources.ic_light_mode
import multiplatform_app.composeapp.generated.resources.ic_search
import org.company.app.AppNavigation
import org.company.app.theme.LocalThemeIsDark
import org.company.app.utils.toFixedString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun MainScreen(navigateToLogin: () -> Unit) {
    val viewModel = remember { MainViewModel() }

    LaunchedEffect(viewModel.navigation.value) {
        if (viewModel.navigation.value == AppNavigation.Login) {
            navigateToLogin()
        }
    }

    Column(modifier = Modifier.verticalScroll(rememberScrollState(), enabled = true)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                modifier = Modifier.width(250.dp),
                value = viewModel.inputTicker.value,
                onValueChange = { viewModel.inputTicker.value = it },
            )

            IconButton(
                modifier = Modifier.padding(horizontal = 15.dp).size(20.dp),
                onClick = { viewModel.onSearchTicker() },
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_search),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                    contentDescription = "search button icon",
                )
            }

            Text("현재가 : " + viewModel.currentPrice.value?.toFixedString(2))

            Spacer(modifier = Modifier.weight(1f))

            var isDark by LocalThemeIsDark.current
            val icon = remember(isDark) {
                if (isDark) Res.drawable.ic_light_mode
                else Res.drawable.ic_dark_mode
            }

            ElevatedButton(
                modifier = Modifier,
                onClick = { isDark = !isDark },
                content = {
                    Icon(vectorResource(icon), contentDescription = null)
                }
            )

            ElevatedButton(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .widthIn(min = 100.dp),
                onClick = { viewModel.logout() },
                content = { Text("로그아웃") }
            )
        }

        Row {
            OrderBookView(viewModel)
        }
    }
}

@Composable
fun OrderBookView(viewModel: MainViewModel) {
    Column(modifier = Modifier.width(300.dp)) {
        Row {
            Text(
                "잔량",
                modifier = Modifier.weight(1f).fillMaxHeight().padding(end = 10.dp),
                textAlign = TextAlign.End
            )
            Text(
                "호가",
                modifier = Modifier.weight(1f).fillMaxHeight().padding(end = 10.dp),
                textAlign = TextAlign.End
            )
        }

        viewModel.orderBook.value?.orderBookUnits?.fastForEachReversed {
            OrderBookItemRow(
                it.askSize,
                it.askPrice,
                viewModel.currentPrice.value ?: 0.0,
                isAskPrice = true
            )
        }

        viewModel.orderBook.value?.orderBookUnits?.forEach {
            OrderBookItemRow(
                it.bidSize,
                it.bidPrice,
                viewModel.currentPrice.value ?: 0.0,
                isAskPrice = false
            )
        }
    }
    Column(modifier = Modifier.width(300.dp)) {
        Row {
            Text(
                "체결가",
                modifier = Modifier.weight(1f).fillMaxHeight().padding(start = 10.dp),
                textAlign = TextAlign.Start
            )
            Text(
                "체결량",
                modifier = Modifier.weight(1f).fillMaxHeight().padding(end = 10.dp),
                textAlign = TextAlign.End
            )
        }

        viewModel.trades.value.fastForEachReversed {
            TradeItemRow(
                it.tradePrice,
                it.tradeVolume,
                it.change
            )
        }

    }
}

@Composable
fun TradeItemRow(tradePrice: Double, tradeVolume: Double, change: String) {
    val tradePriceColor = if (change == "RISE") Color.Red else Color.Blue

    Row(
        modifier = Modifier.size(width = 300.dp, height = 30.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = tradePrice.toFixedString(2),
            modifier = Modifier.weight(1f).fillMaxHeight()
                .padding(start = 10.dp),
            textAlign = TextAlign.Start,
            style = TextStyle(color = tradePriceColor, fontSize = 13.sp)
        )

        Text(
            text = tradeVolume.toFixedString(3),
            modifier = Modifier.weight(1f).fillMaxHeight()
                .padding(end = 10.dp),
            textAlign = TextAlign.End,
            style = TextStyle(fontSize = 13.sp)
        )
    }
}

@Composable
fun OrderBookItemRow(qty: Double, price: Double, currentPrice: Double, isAskPrice: Boolean) {
    val isCurrentPriceItem = currentPrice == price
    val backgroundColor = if (isAskPrice) Color(0xFFEEF3FF) else Color(0xFFFFF1F0)

    Row(
        modifier = Modifier
            .border(
                border = BorderStroke(
                    if (isCurrentPriceItem) 3.dp else 1.dp,
                    if (isCurrentPriceItem) Color.Red else Color.White
                )
            )
            .size(width = 300.dp, height = 30.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = qty.toFixedString(3),
            modifier = Modifier.weight(1f).fillMaxHeight().background(backgroundColor)
                .padding(end = 10.dp),
            textAlign = TextAlign.End,
            color = Color.Black
        )

        Spacer(
            modifier = Modifier.width(1.dp).fillMaxHeight()
                .background(Color.White)
        )

        Text(
            text = price.toFixedString(2),
            modifier = Modifier.weight(1f).fillMaxHeight().background(backgroundColor)
                .padding(end = 10.dp),
            textAlign = TextAlign.End,
            color = Color.Black
        )
    }
}