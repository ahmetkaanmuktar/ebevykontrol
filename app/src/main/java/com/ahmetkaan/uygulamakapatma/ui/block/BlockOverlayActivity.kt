package com.ahmetkaan.uygulamakapatma.ui.block

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.HourglassBottom
import androidx.compose.material.icons.outlined.LockClock
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.ahmetkaan.uygulamakapatma.ParentalControlApp
import com.ahmetkaan.uygulamakapatma.R
import com.ahmetkaan.uygulamakapatma.data.repository.AppLimitRepository
import com.ahmetkaan.uygulamakapatma.data.repository.ParentSettingsRepository
import com.ahmetkaan.uygulamakapatma.data.repository.PinVerificationResult
import com.ahmetkaan.uygulamakapatma.ui.theme.AppColors
import com.ahmetkaan.uygulamakapatma.ui.theme.ParentalControlTheme
import com.ahmetkaan.uygulamakapatma.util.BiometricAuthHelper
import kotlinx.coroutines.launch

/**
 * 2027 Dijital Mola & Kilit Ekranı.
 * Nefes alan neon kalkan animasyonu, göz yormayan OLED derin siyah tasarım
 * ve tek dokunuşla parmak izi / PIN ile ek süre tanıma desteği içerir.
 */
class BlockOverlayActivity : FragmentActivity() {

    companion object {
        const val EXTRA_PACKAGE_NAME = "extra_package_name"
        const val EXTRA_APP_NAME = "extra_app_name"
        const val EXTRA_USED_MINUTES = "extra_used_minutes"
        const val EXTRA_LIMIT_MINUTES = "extra_limit_minutes"

        fun start(
            context: Context,
            packageName: String,
            appName: String = "",
            usedMinutes: Int = 0,
            limitMinutes: Int = 0
        ) {
            val intent = Intent(context, BlockOverlayActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                putExtra(EXTRA_PACKAGE_NAME, packageName)
                putExtra(EXTRA_APP_NAME, appName)
                putExtra(EXTRA_USED_MINUTES, usedMinutes)
                putExtra(EXTRA_LIMIT_MINUTES, limitMinutes)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Güvenlik: PIN girilirken ekran kaydını veya ekran görüntüsünü engelle
        window.setFlags(
            android.view.WindowManager.LayoutParams.FLAG_SECURE,
            android.view.WindowManager.LayoutParams.FLAG_SECURE
        )

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                sendToHome()
            }
        })

        val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: ""
        val appName = intent.getStringExtra(EXTRA_APP_NAME).takeIf { !it.isNullOrBlank() }
            ?: getAppLabel(packageName)
        val usedMinutes = intent.getIntExtra(EXTRA_USED_MINUTES, 0)
        val limitMinutes = intent.getIntExtra(EXTRA_LIMIT_MINUTES, 0)

        val app = application as ParentalControlApp
        val parentRepo = ParentSettingsRepository(app.database.parentSettingsDao())
        val limitRepo = AppLimitRepository(app.database.appLimitDao())

        setContent {
            ParentalControlTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AppColors.BackgroundDark
                ) {
                    BlockOverlayScreen(
                        appName = appName,
                        packageName = packageName,
                        usedMinutes = usedMinutes,
                        limitMinutes = limitMinutes,
                        onGoHome = { sendToHome() },
                        parentRepo = parentRepo,
                        limitRepo = limitRepo,
                        onExtensionGranted = {
                            finish()
                        }
                    )
                }
            }
        }
    }

    private fun sendToHome() {
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(homeIntent)
        finish()
    }

    private fun getAppLabel(pkg: String): String {
        return try {
            val info = packageManager.getApplicationInfo(pkg, 0)
            packageManager.getApplicationLabel(info).toString()
        } catch (e: Exception) {
            pkg
        }
    }
}

@Composable
private fun BlockOverlayScreen(
    appName: String,
    packageName: String,
    usedMinutes: Int,
    limitMinutes: Int,
    onGoHome: () -> Unit,
    parentRepo: ParentSettingsRepository,
    limitRepo: AppLimitRepository,
    onExtensionGranted: () -> Unit
) {
    var showPinDialog by remember { mutableStateOf(false) }

    // Nefes alan neon nabız (pulse) animasyonu
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Nefes Alan Neon Kalkan İkonu
        Box(
            modifier = Modifier
                .size(110.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(AppColors.CrimsonAlert.copy(alpha = 0.12f))
                .border(2.dp, AppColors.CrimsonAlert.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.LockClock,
                contentDescription = null,
                tint = AppColors.CrimsonAlert,
                modifier = Modifier.size(56.dp)
            )
        }

        Spacer(modifier = Modifier.height(26.dp))

        Text(
            text = androidx.compose.ui.res.stringResource(com.ahmetkaan.uygulamakapatma.R.string.overlay_break_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = AppColors.TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = androidx.compose.ui.res.stringResource(
                com.ahmetkaan.uygulamakapatma.R.string.overlay_break_desc,
                appName
            ),
            fontSize = 14.sp,
            color = AppColors.TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Kullanım Özet Kartı
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(AppColors.GlassSurface)
                .border(1.dp, AppColors.GlassBorder, RoundedCornerShape(18.dp))
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = androidx.compose.ui.res.stringResource(com.ahmetkaan.uygulamakapatma.R.string.overlay_used_today),
                        fontSize = 12.sp,
                        color = AppColors.TextTertiary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$usedMinutes dk",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.CrimsonAlert
                    )
                }

                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .width(1.dp)
                        .background(AppColors.GlassBorder)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = androidx.compose.ui.res.stringResource(com.ahmetkaan.uygulamakapatma.R.string.overlay_daily_limit),
                        fontSize = 12.sp,
                        color = AppColors.TextTertiary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$limitMinutes dk",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.CyberCyan
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Ana Ekrana Dön Butonu
        Button(
            onClick = onGoHome,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryIndigo)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = androidx.compose.ui.res.stringResource(com.ahmetkaan.uygulamakapatma.R.string.overlay_go_home),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Ebeveyn İzniyle Ek Süre Butonu
        OutlinedButton(
            onClick = { showPinDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.GlassBorderActive)
        ) {
            Icon(
                imageVector = Icons.Outlined.Security,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = AppColors.CyberCyan
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = androidx.compose.ui.res.stringResource(com.ahmetkaan.uygulamakapatma.R.string.overlay_add_time_btn),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.CyberCyan
            )
        }
    }

    if (showPinDialog) {
        ParentPinDialog(
            parentRepo = parentRepo,
            onDismiss = { showPinDialog = false },
            onSuccess = {
                onExtensionGranted()
                showPinDialog = false
            },
            packageName = packageName,
            limitRepo = limitRepo
        )
    }
}

@Composable
private fun ParentPinDialog(
    parentRepo: ParentSettingsRepository,
    limitRepo: AppLimitRepository,
    packageName: String,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var pinText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val hasBiometric = remember { BiometricAuthHelper.isBiometricAvailable(context) }

    fun triggerBiometric() {
        if (activity != null && hasBiometric) {
            BiometricAuthHelper.promptBiometric(
                activity = activity,
                title = "Ek Süre Onayı",
                subtitle = "15 dakika ek süre vermek için parmak izinizi dokundurun",
                onSuccess = {
                    scope.launch {
                        limitRepo.addExtensionMinutes(packageName, 15)
                        Toast.makeText(context, "+15 dakika ek süre verildi!", Toast.LENGTH_SHORT).show()
                        onSuccess()
                    }
                },
                onError = { err -> errorMessage = err }
            )
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AppColors.GlassSurfaceElevated,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.border(1.dp, AppColors.GlassBorder, RoundedCornerShape(20.dp)),
        title = {
            Text(
                text = androidx.compose.ui.res.stringResource(com.ahmetkaan.uygulamakapatma.R.string.overlay_parent_permission),
                color = AppColors.TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column {
                Text(
                    text = androidx.compose.ui.res.stringResource(com.ahmetkaan.uygulamakapatma.R.string.overlay_parent_permission_desc),
                    fontSize = 13.sp,
                    color = AppColors.TextSecondary,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

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
                        placeholder = { Text("PIN Kodu", color = AppColors.TextTertiary) },
                        isError = errorMessage != null,
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
                        when (val result = parentRepo.verifyPin(pinText)) {
                            is PinVerificationResult.Success -> {
                                limitRepo.addExtensionMinutes(packageName, 15)
                                Toast.makeText(context, "+15 dakika ek süre tanımlandı!", Toast.LENGTH_SHORT).show()
                                onSuccess()
                            }
                            is PinVerificationResult.LockedOut -> {
                                errorMessage = "Çok fazla hatalı deneme! Lütfen ${result.remainingSeconds} sn bekleyin."
                            }
                            is PinVerificationResult.InvalidPin -> {
                                errorMessage = "Hatalı PIN kodu!"
                            }
                            is PinVerificationResult.NotConfigured -> {
                                errorMessage = "Ebeveyn PIN kodu henüz ayarlanmamış."
                            }
                        }
                    }
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryIndigo)
            ) {
                Text(
                    text = androidx.compose.ui.res.stringResource(com.ahmetkaan.uygulamakapatma.R.string.overlay_verify_and_add),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = androidx.compose.ui.res.stringResource(com.ahmetkaan.uygulamakapatma.R.string.overlay_cancel),
                    color = AppColors.TextSecondary
                )
            }
        }
    )
}
