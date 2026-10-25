package com.ahmetkaan.uygulamakapatma.ui.pin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.ahmetkaan.uygulamakapatma.data.repository.ParentSettingsRepository
import com.ahmetkaan.uygulamakapatma.data.repository.PinVerificationResult
import com.ahmetkaan.uygulamakapatma.ui.theme.AppColors
import com.ahmetkaan.uygulamakapatma.util.BiometricAuthHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 2027 FinTech Standardında Ebeveyn PIN & Biyometrik Giriş Ekranı.
 */
@Composable
fun PinLoginScreen(
    parentRepo: ParentSettingsRepository,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    var pinText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isShakeError by remember { mutableStateOf(false) }
    var showRecoveryDialog by remember { mutableStateOf(false) }
    var isVerifying by remember { mutableStateOf(false) }

    val hasBiometric = remember { BiometricAuthHelper.isBiometricAvailable(context) }
    val activity = context as? FragmentActivity

    fun triggerBiometricAuth() {
        if (activity != null && hasBiometric) {
            BiometricAuthHelper.promptBiometric(
                activity = activity,
                title = "Ebeveyn Girişi",
                subtitle = "Kontrol paneline erişmek için parmak izinizi kullanın",
                onSuccess = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLoginSuccess()
                },
                onError = { err ->
                    errorMessage = err
                }
            )
        }
    }

    // Ekran ilk açıldığında biyometrik donanım varsa otomatik çağır
    LaunchedEffect(Unit) {
        if (hasBiometric && activity != null) {
            delay(250)
            triggerBiometricAuth()
        }
    }

    fun submitPin(pin: String) {
        if (isVerifying) return
        isVerifying = true

        scope.launch {
            when (val result = parentRepo.verifyPin(pin)) {
                is PinVerificationResult.Success -> {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLoginSuccess()
                }
                is PinVerificationResult.LockedOut -> {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    isShakeError = true
                    errorMessage = "Çok fazla deneme! ${result.remainingSeconds} sn bekleyin."
                    pinText = ""
                    delay(500)
                    isShakeError = false
                }
                is PinVerificationResult.InvalidPin -> {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    isShakeError = true
                    errorMessage = "Hatalı PIN kodu! Tekrar deneyin."
                    pinText = ""
                    delay(500)
                    isShakeError = false
                }
                is PinVerificationResult.NotConfigured -> {
                    onLoginSuccess()
                }
            }
            isVerifying = false
        }
    }

    Scaffold(containerColor = AppColors.BackgroundDark) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Üst Bölüm: Kalkan & Başlık
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(AppColors.PrimaryIndigo.copy(alpha = 0.15f))
                        .border(1.dp, AppColors.PrimaryIndigoLight.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Security,
                        contentDescription = null,
                        tint = AppColors.CyberCyan,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Ebeveyn Girişi",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Kontrol paneline erişmek için PIN kodunuzu girin",
                    fontSize = 13.sp,
                    color = AppColors.TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // PIN Noktaları (Görsel Gösterge)
                PinDotsIndicator(
                    pinLength = pinText.length,
                    maxDigits = 4,
                    isError = isShakeError
                )

                // Hata Mesajı
                AnimatedVisibility(visible = errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = AppColors.CrimsonAlert,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
            }

            // Alt Bölüm: 2027 Dokunmatik Numpad
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ModernNumpad(
                    onDigitClick = { digit ->
                        if (pinText.length < 6) {
                            val newPin = pinText + digit
                            pinText = newPin
                            errorMessage = null
                            if (newPin.length == 4) {
                                submitPin(newPin)
                            }
                        }
                    },
                    onDeleteClick = {
                        if (pinText.isNotEmpty()) {
                            pinText = pinText.dropLast(1)
                            errorMessage = null
                        }
                    },
                    onBiometricClick = if (hasBiometric) {
                        { triggerBiometricAuth() }
                    } else null,
                    enabled = !isVerifying
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = { showRecoveryDialog = true }) {
                    Text(
                        text = "PIN Kodumu Unuttum",
                        color = AppColors.CyberCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showRecoveryDialog) {
        PinVerificationDialog(
            parentRepo = parentRepo,
            onSuccess = {
                showRecoveryDialog = false
                onLoginSuccess()
            },
            onDismiss = { showRecoveryDialog = false }
        )
    }
}
