package com.jbappz.cryptocoin.util

expect fun formatCoinPrice(amount: Double, showDecimal: Boolean = true): String

expect fun formatCoinUnit(amount: Double, symbol: String): String

expect fun formatCoinPercentage(amount: Double): String