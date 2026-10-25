package com.ahmetkaan.uygulamakapatma.ui.pin

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ahmetkaan.uygulamakapatma.ui.theme.AppColors
import kotlin.math.roundToInt

/**
 * 2027 FinTech Tarzı PIN Gösterge Noktaları (Dots)
 * Hatalı girişte sağa sola sarsılma (shake) animasyonu ve neon ışıltı barındırır.
 */
@Composable
fun PinDotsIndicator(
    pinLength: Int,
    maxDigits: Int = 4,
    isError: Boolean = false
) {
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(isError) {
        if (isError) {
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 400
                    -24f at 50
                    24f at 100
                    -16f at 150
                    16f at 200
                    -8f at 250
                    8f at 300
                    0f at 400
                }
            )
        }
    }

    Row(
        modifier = Modifier
            .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(maxDigits) { index ->
            val isFilled = index < pinLength

            val dotColor = when {
                isError -> AppColors.CrimsonAlert
                isFilled -> AppColors.CyberCyan
                else -> Color(0x334E557A)
            }

            Box(
                modifier = Modifier
                    .size(if (isFilled) 18.dp else 16.dp)
                    .clip(CircleShape)
                    .background(if (isFilled) dotColor else Color.Transparent)
                    .border(
                        2.dp,
                        dotColor,
                        CircleShape
                    )
            )
        }
    }
}

/**
 * 2027 Dokunmatik ve Haptik Destekli Özel Numpad
 */
@Composable
fun ModernNumpad(
    onDigitClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onBiometricClick: (() -> Unit)? = null,
    enabled: Boolean = true
) {
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val rows = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9")
        )

        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { digit ->
                    NumpadKey(
                        text = digit,
                        onClick = {
                            if (enabled) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onDigitClick(digit)
                            }
                        }
                    )
                }
            }
        }

        // Son Satır: Biyometrik (opsiyonel), 0, Silme
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sol Tuş: Biyometrik veya Boşluk
            if (onBiometricClick != null) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(AppColors.GlassSurface)
                        .border(1.dp, AppColors.GlassBorder, CircleShape)
                        .clickable {
                            if (enabled) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onBiometricClick()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Fingerprint,
                        contentDescription = "Biyometrik Giriş",
                        tint = AppColors.CyberCyan,
                        modifier = Modifier.size(34.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(72.dp))
            }

            // Orta Tuş: 0
            NumpadKey(
                text = "0",
                onClick = {
                    if (enabled) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onDigitClick("0")
                    }
                }
            )

            // Sağ Tuş: Geri Silme (Backspace)
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(AppColors.GlassSurface)
                    .border(1.dp, AppColors.GlassBorder, CircleShape)
                    .clickable {
                        if (enabled) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onDeleteClick()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Backspace,
                    contentDescription = "Sil",
                    tint = AppColors.TextSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun NumpadKey(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(AppColors.GlassSurface)
            .border(1.dp, AppColors.GlassBorder, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary
        )
    }
}
