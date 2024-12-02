package org.company.app.utils

import com.ionspin.kotlin.bignum.decimal.BigDecimal

fun Double.toFixedString(digits: Int): String {
    var result = BigDecimal.fromDouble(this)
        .scale(digits.toLong())
        .toPlainString()

    if (digits > 0) {
        val parts = result.split(".")
        if (parts.size == 1) {
            result += "." + "0".repeat(digits)
        } else {
            val fractionalPart = parts[1]
            val zerosToAdd = digits - fractionalPart.length
            if (zerosToAdd > 0) {
                result += "0".repeat(zerosToAdd)
            }
        }
    }

    return result
}