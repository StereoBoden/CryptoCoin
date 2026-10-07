package com.jbappz.cryptocoin.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Light Theme Colors
val PrimaryLight = Color(0xFF6200EE)
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFBB86FC)
val OnPrimaryContainerLight = Color(0xFF3700B3)

val SecondaryLight = Color(0xFF03DAC6)
val OnSecondaryLight = Color(0xFF000000)

val BackgroundLight = Color(0xFFF8F9FA)
val OnBackgroundLight = Color(0xFF1C1B1F)

val SurfaceLight = Color(0xFFFFFFFF)
val OnSurfaceLight = Color(0xFF1C1B1F)

val ProfitGreenLight = Color(0xFF2E7D32)
val LossRedLight = Color(0xFFD32F2F)

// Dark Theme Colors
val PrimaryDark = Color(0xFFBB86FC)
val OnPrimaryDark = Color(0xFF000000)
val PrimaryContainerDark = Color(0xFF3700B3)
val OnPrimaryContainerDark = Color(0xFFBB86FC)

val SecondaryDark = Color(0xFF03DAC6)
val OnSecondaryDark = Color(0xFF000000)

val BackgroundDark = Color(0xFF121212)
val OnBackgroundDark = Color(0xFFE6E1E5)

val SurfaceDark = Color(0xFF1E1E1E)
val OnSurfaceDark = Color(0xFFE6E1E5)

val ProfitGreenDark = Color(0xFF4CAF50)
val LossRedDark = Color(0xFFEF5350)

@Immutable
data class ExtendedColors(
    val profitGreen: Color,
    val lossRed: Color,
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        profitGreen = ProfitGreenLight,
        lossRed = LossRedLight,
    )
}
