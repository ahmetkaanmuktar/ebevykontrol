package com.ahmetkaan.uygulamakapatma.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ahmetkaan.uygulamakapatma.data.repository.ParentSettingsRepository
import com.ahmetkaan.uygulamakapatma.ui.pin.DEFAULT_SECURITY_QUESTIONS
import com.ahmetkaan.uygulamakapatma.ui.pin.ModernNumpad
import com.ahmetkaan.uygulamakapatma.ui.pin.PinDotsIndicator
import com.ahmetkaan.uygulamakapatma.ui.theme.AppColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class PinSetupStage {
    ENTER_PIN,
    CONFIRM_PIN,
    SECURITY_QUESTION
}

/**
 * 2027 Numpad Destekli Onboarding PIN Belirleme Ekranı.
 * İzinlerden önce ebeveyn sahipliğini kurar.
 */
@Composable
fun OnboardingPinScreen(
    parentRepo: ParentSettingsRepository,
    onPinConfigured: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    var stage by remember { mutableStateOf(PinSetupStage.ENTER_PIN) }
    var firstPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var isShakeError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Kurtarma Sorusu Durumu
    var selectedQuestion by remember { mutableStateOf(DEFAULT_SECURITY_QUESTIONS[0]) }
    var securityAnswer by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    fun handleDigit(digit: String) {
        when (stage) {
            PinSetupStage.ENTER_PIN -> {
                if (firstPin.length < 4) {
                    val newPin = firstPin + digit
                    firstPin = newPin
                    errorMessage = null
                    if (newPin.length == 4) {
                        scope.launch {
                            delay(200)
                            stage = PinSetupStage.CONFIRM_PIN
                        }
                    }
                }
            }
            PinSetupStage.CONFIRM_PIN -> {
                if (confirmPin.length < 4) {
                    val newPin = confirmPin + digit
                    confirmPin = newPin
                    errorMessage = null
                    if (newPin.length == 4) {
                        if (newPin == firstPin) {
                            scope.launch {
                                delay(200)
                                stage = PinSetupStage.SECURITY_QUESTION
                            }
                        } else {
                            scope.launch {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                isShakeError = true
                                errorMessage = "PIN kodları uyuşmadı! Tekrar deneyin."
                                delay(500)
                                isShakeError = false
                                confirmPin = ""
                            }
                        }
                    }
                }
            }
            else -> {}
        }
    }

    fun handleDelete() {
        when (stage) {
            PinSetupStage.ENTER_PIN -> {
                if (firstPin.isNotEmpty()) firstPin = firstPin.dropLast(1)
            }
            PinSetupStage.CONFIRM_PIN -> {
                if (confirmPin.isNotEmpty()) {
                    confirmPin = confirmPin.dropLast(1)
                } else {
                    // Boşsa ilk adıma geri dön
                    firstPin = ""
                    stage = PinSetupStage.ENTER_PIN
                }
            }
            else -> {}
        }
        errorMessage = null
    }

    Scaffold(
        containerColor = AppColors.BackgroundDark,
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = stage,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "pinStage"
            ) { currentStage ->
                when (currentStage) {
                    PinSetupStage.ENTER_PIN, PinSetupStage.CONFIRM_PIN -> {
                        val isConfirm = currentStage == PinSetupStage.CONFIRM_PIN
                        val currentLength = if (isConfirm) confirmPin.length else firstPin.length

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(top = 28.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(76.dp)
                                        .clip(CircleShape)
                                        .background(AppColors.PrimaryIndigo.copy(alpha = 0.15f))
                                        .border(1.dp, AppColors.GlassBorderActive, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Lock,
                                        contentDescription = null,
                                        tint = AppColors.CyberCyan,
                                        modifier = Modifier.size(38.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                Text(
                                    text = if (isConfirm) "PIN Kodunu Doğrulayın" else "Ebeveyn PIN Kodu Belirleyin",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.TextPrimary,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = if (isConfirm) "Az önce girdiğiniz 4 haneli PIN kodunu tekrar yazın."
                                    else "Çocuğunuzun süre sınırlarını değiştirememesi için 4 haneli güvenli bir kod seçin.",
                                    fontSize = 13.sp,
                                    color = AppColors.TextSecondary,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp
                                )

                                Spacer(modifier = Modifier.height(26.dp))

                                PinDotsIndicator(
                                    pinLength = currentLength,
                                    maxDigits = 4,
                                    isError = isShakeError
                                )

                                AnimatedVisibility(visible = errorMessage != null) {
                                    Text(
                                        text = errorMessage ?: "",
                                        color = AppColors.CrimsonAlert,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(top = 10.dp)
                                    )
                                }
                            }

                            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                                ModernNumpad(
                                    onDigitClick = { handleDigit(it) },
                                    onDeleteClick = { handleDelete() }
                                )
                            }
                        }
                    }

                    PinSetupStage.SECURITY_QUESTION -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(top = 28.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(76.dp)
                                        .clip(CircleShape)
                                        .background(AppColors.CyberCyan.copy(alpha = 0.15f))
                                        .border(1.dp, AppColors.CyberCyan.copy(alpha = 0.4f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.QuestionAnswer,
                                        contentDescription = null,
                                        tint = AppColors.CyberCyan,
                                        modifier = Modifier.size(38.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                Text(
                                    text = "Kurtarma Sorusu",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.TextPrimary,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "PIN kodunu unutursanız ayarları sıfırlayabilmeniz için bir güvenlik sorusu seçin.",
                                    fontSize = 13.sp,
                                    color = AppColors.TextSecondary,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // Soru Kartları
                                DEFAULT_SECURITY_QUESTIONS.take(3).forEach { question ->
                                    val isSelected = question == selectedQuestion
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSelected) AppColors.PrimaryIndigo.copy(alpha = 0.25f) else AppColors.GlassSurface)
                                            .border(
                                                1.dp,
                                                if (isSelected) AppColors.PrimaryIndigoLight else AppColors.GlassBorder,
                                                RoundedCornerShape(12.dp)
                                            )
                                            .clickable { selectedQuestion = question }
                                            .padding(14.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = question,
                                                fontSize = 13.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) Color.White else AppColors.TextSecondary
                                            )
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Check,
                                                    contentDescription = null,
                                                    tint = AppColors.CyberCyan,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                // Cevap Alanı
                                OutlinedTextField(
                                    value = securityAnswer,
                                    onValueChange = {
                                        securityAnswer = it
                                        errorMessage = null
                                    },
                                    placeholder = { Text("Cevabınızı buraya yazın", color = AppColors.TextTertiary) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = AppColors.TextPrimary,
                                        unfocusedTextColor = AppColors.TextPrimary,
                                        focusedBorderColor = AppColors.CyberCyan,
                                        unfocusedBorderColor = AppColors.GlassBorder,
                                        focusedContainerColor = AppColors.GlassSurface,
                                        unfocusedContainerColor = AppColors.GlassSurface
                                    )
                                )

                                AnimatedVisibility(visible = errorMessage != null) {
                                    Text(
                                        text = errorMessage ?: "",
                                        color = AppColors.CrimsonAlert,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }

                            // Kaydet Butonu
                            Column(modifier = Modifier.padding(bottom = 24.dp)) {
                                Button(
                                    onClick = {
                                        if (securityAnswer.trim().length < 2) {
                                            errorMessage = "Lütfen geçerli bir cevap girin."
                                            return@Button
                                        }

                                        isSaving = true
                                        scope.launch {
                                            try {
                                                parentRepo.setupPin(
                                                    rawPin = firstPin,
                                                    securityQuestion = selectedQuestion,
                                                    securityAnswer = securityAnswer
                                                )
                                                onPinConfigured()
                                            } catch (e: Exception) {
                                                errorMessage = "Kayıt hatası: ${e.localizedMessage}"
                                            } finally {
                                                isSaving = false
                                            }
                                        }
                                    },
                                    enabled = !isSaving,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryIndigo)
                                ) {
                                    Text(
                                        text = if (isSaving) "Kaydediliyor..." else "PIN'i Kaydet & İzinlere Geç",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
