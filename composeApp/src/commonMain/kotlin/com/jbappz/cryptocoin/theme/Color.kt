package com.jbappz.cryptocoin.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Light Theme Colors
internal val PrimaryLight = Color(0xFF485F84)
internal val InversePrimaryLight = Color(0xFFB0C7F1)
internal val OnPrimaryLight = Color(0xFFFFFFFF)
internal val PrimaryContainerLight = Color(0xFFD8E2F3)
internal val OnPrimaryContainerLight = Color(0xFF0E1D35)

internal val SecondaryLight = Color(0xFF2B6485)
internal val OnSecondaryLight = Color(0xFFFFFFFF)

internal val BackgroundLight = Color(0xFFF8F9FA)
internal val OnBackgroundLight = Color(0xFF1C1B1F)

internal val SurfaceLight = Color(0xFFFFFFFF)
internal val OnSurfaceLight = Color(0xFF1C1B1F)

internal val ProfitGreenLight = Color(0xFF2E7D32)
internal val LossRedLight = Color(0xFFD32F2F)

// Dark Theme Colors
internal val PrimaryDark = Color(0xFFB0C7F1)
internal val InversePrimaryDark = Color(0xFF485F84)
internal val OnPrimaryDark = Color(0xFF132238)
internal val PrimaryContainerDark = Color(0xFF30476E)
internal val OnPrimaryContainerDark = Color(0xFFD8E2F3)

internal val SecondaryDark = Color(0xFF98CDF2)
internal val OnSecondaryDark = Color(0xFF0C1C30)

internal val BackgroundDark = Color(0xFF121212)
internal val OnBackgroundDark = Color(0xFFE6E1E5)

internal val SurfaceDark = Color(0xFF1E1E1E)
internal val OnSurfaceDark = Color(0xFFE6E1E5)

internal val ProfitGreenDark = Color(0xFF4CAF50)
internal val LossRedDark = Color(0xFFEF5350)

@Immutable
data class ExtendedColors(
    val profitGreen: Color,
    val lossRed: Color,
)

internal val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        profitGreen = ProfitGreenLight,
        lossRed = LossRedLight,
    )
}
