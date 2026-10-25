package com.ahmetkaan.uygulamakapatma.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * 2027+ Fütüristik Cyber-Glass & Deep OLED Renk Sistemi
 */
object AppColors {
    val BackgroundDark = Color(0xFF090A0F)
    val BackgroundTopGradient = Color(0xFF121424)
    val GlassSurface = Color(0xFF131522)
    val GlassSurfaceElevated = Color(0xFF1C1E2F)
    val GlassBorder = Color(0x334E557A)
    val GlassBorderActive = Color(0x666366F1)
    val GlassBorderGlow = Color(0x80818CF8)

    val PrimaryIndigo = Color(0xFF6366F1)
    val PrimaryIndigoLight = Color(0xFF818CF8)
    val PrimaryIndigoDark = Color(0xFF4F46E5)

    val CyberCyan = Color(0xFF06B6D4)
    val CyberTeal = Color(0xFF14B8A6)
    val EmeraldSuccess = Color(0xFF10B981)
    val AmberWarning = Color(0xFFF59E0B)
    val CrimsonAlert = Color(0xFFF43F5E)
    val CrimsonAlertSoft = Color(0x2DF43F5E)

    val TextPrimary = Color(0xFFF8FAFC)
    val TextSecondary = Color(0xFF94A3B8)
    val TextTertiary = Color(0xFF64748B)

    // Fütüristik Degrade Fırçaları
    val PrimaryGradient = Brush.horizontalGradient(
        listOf(Color(0xFF6366F1), Color(0xFF8B5CF6), Color(0xFF06B6D4))
    )
    val CyanTealGradient = Brush.horizontalGradient(
        listOf(Color(0xFF06B6D4), Color(0xFF14B8A6))
    )
    val AlertGradient = Brush.horizontalGradient(
        listOf(Color(0xFFF43F5E), Color(0xFFE11D48))
    )
    val SuccessGradient = Brush.horizontalGradient(
        listOf(Color(0xFF10B981), Color(0xFF059669))
    )
    val CardGradient = Brush.verticalGradient(
        listOf(Color(0xFF1A1C2E), Color(0xFF121422))
    )
    val BackgroundGradient = Brush.verticalGradient(
        listOf(BackgroundTopGradient, BackgroundDark)
    )
    val GlassBorderGradient = Brush.linearGradient(
        listOf(Color(0x66818CF8), Color(0x1A06B6D4), Color(0x334E557A))
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = AppColors.PrimaryIndigo,
    secondary = AppColors.CyberCyan,
    tertiary = AppColors.CyberTeal,
    background = AppColors.BackgroundDark,
    surface = AppColors.GlassSurface,
    surfaceVariant = AppColors.GlassSurfaceElevated,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = AppColors.TextPrimary,
    onSurface = AppColors.TextPrimary,
    error = AppColors.CrimsonAlert
)

private val LightColorScheme = lightColorScheme(
    primary = AppColors.PrimaryIndigoDark,
    secondary = AppColors.CyberTeal,
    tertiary = AppColors.PrimaryIndigoLight,
    background = Color(0xFFF8FAFC),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    error = AppColors.CrimsonAlert
)

/**
 * 2027+ Standartlarında Material 3 Teması
 */
@Composable
fun ParentalControlTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
