package com.ahmetkaan.uygulamakapatma.ui.pin

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.ahmetkaan.uygulamakapatma.data.repository.ParentSettingsRepository
import com.ahmetkaan.uygulamakapatma.data.repository.PinVerificationResult
import com.ahmetkaan.uygulamakapatma.ui.theme.AppColors
import com.ahmetkaan.uygulamakapatma.util.BiometricAuthHelper
import kotlinx.coroutines.launch

/**
 * 2027 Ebeveyn Onay ve PIN Doğrulama Penceresi.
 * Parmak izi / Yüz tanıma ve PIN kurtarma seçenekleriyle donatılmıştır.
 */
@Composable
fun PinVerificationDialog(
    parentRepo: ParentSettingsRepository,
    onSuccess: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val hasBiometric = remember { BiometricAuthHelper.isBiometricAvailable(context) }

    var isRecoveryMode by remember { mutableStateOf(false) }
    var pinText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    var securityQuestion by remember { mutableStateOf<String?>(null) }
    var answerText by remember { mutableStateOf("") }
    var newPinText by remember { mutableStateOf("") }

    fun triggerBiometric() {
        if (activity != null && hasBiometric) {
            BiometricAuthHelper.promptBiometric(
                activity = activity,
                title = "İşlem Onayı",
                subtitle = "Değişikliği onaylamak için parmak izinizi dokundurun",
                onSuccess = onSuccess,
                onError = { err -> errorMessage = err }
            )
        }
    }

    LaunchedEffect(isRecoveryMode) {
        if (isRecoveryMode) {
            securityQuestion = parentRepo.getSecurityQuestion()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AppColors.GlassSurfaceElevated,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.border(1.dp, AppColors.GlassBorder, RoundedCornerShape(20.dp)),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = null,
                    tint = AppColors.CyberCyan,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isRecoveryMode) "PIN Kurtarma" else "Ebeveyn Doğrulaması",
                    color = AppColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column {
                if (!isRecoveryMode) {
                    Text(
                        text = "Kural veya limit değişikliği için PIN kodunuzu girin veya parmak izinizi kullanın.",
                        fontSize = 13.sp,
                        color = AppColors.TextSecondary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = pinText,
                            onValueChange = { if (it.length <= 6) pinText = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            isError = errorMessage != null,
                            placeholder = { Text("4-6 haneli PIN", color = AppColors.TextTertiary) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = AppColors.TextPrimary,
                                unfocusedTextColor = AppColors.TextPrimary,
                                focusedBorderColor = AppColors.PrimaryIndigo,
                                unfocusedBorderColor = AppColors.GlassBorder
                            )
                        )

                        if (hasBiometric) {
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { triggerBiometric() },
                                modifier = Modifier
                                    .size(52.dp)
                                    .border(1.dp, AppColors.GlassBorderActive, RoundedCornerShape(12.dp))
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Fingerprint,
                                    contentDescription = "Parmak İzi",
                                    tint = AppColors.CyberCyan,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    TextButton(
                        onClick = {
                            isRecoveryMode = true
                            errorMessage = null
                        }
                    ) {
                        Text(
                            text = "PIN Kodumu Unuttum",
                            color = AppColors.CyberCyan,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    Text(
                        text = "Güvenlik sorusunu yanıtlayarak yeni bir PIN belirleyebilirsiniz.",
                        fontSize = 13.sp,
                        color = AppColors.TextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Soru: ${securityQuestion ?: "Yükleniyor..."}",
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.CyberCyan,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = answerText,
                        onValueChange = { answerText = it },
                        label = { Text("Cevabınız") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = AppColors.TextPrimary,
                            unfocusedTextColor = AppColors.TextPrimary,
                            focusedBorderColor = AppColors.PrimaryIndigo,
                            unfocusedBorderColor = AppColors.GlassBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = newPinText,
                        onValueChange = { if (it.length <= 6) newPinText = it },
                        label = { Text("Yeni PIN (4-6 hane)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = AppColors.TextPrimary,
                            unfocusedTextColor = AppColors.TextPrimary,
                            focusedBorderColor = AppColors.PrimaryIndigo,
                            unfocusedBorderColor = AppColors.GlassBorder
                        )
                    )
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage!!,
                        color = AppColors.CrimsonAlert,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    scope.launch {
                        if (!isRecoveryMode) {
                            when (val res = parentRepo.verifyPin(pinText)) {
                                is PinVerificationResult.Success -> onSuccess()
                                is PinVerificationResult.LockedOut -> {
                                    errorMessage = "Çok fazla hatalı deneme! Lütfen ${res.remainingSeconds} sn bekleyin."
                                }
                                is PinVerificationResult.InvalidPin -> {
                                    errorMessage = "Hatalı PIN kodu!"
                                }
                                is PinVerificationResult.NotConfigured -> onSuccess()
                            }
                        } else {
                            if (newPinText.length < 4) {
                                errorMessage = "Yeni PIN en az 4 haneli olmalıdır."
                                return@launch
                            }
                            val success = parentRepo.resetPinWithSecurityAnswer(answerText, newPinText)
                            if (success) {
                                onSuccess()
                            } else {
                                errorMessage = "Güvenlik sorusu cevabı hatalı!"
                            }
                        }
                    }
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryIndigo)
            ) {
                Text(
                    text = if (isRecoveryMode) "PIN Sıfırla & Onayla" else "Onayla",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    if (isRecoveryMode) {
                        isRecoveryMode = false
                        errorMessage = null
                    } else {
                        onDismiss()
                    }
                }
            ) {
                Text(
                    text = if (isRecoveryMode) "Geri" else "Vazgeç",
                    color = AppColors.TextSecondary
                )
            }
        }
    )
}
