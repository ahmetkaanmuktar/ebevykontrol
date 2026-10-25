package com.ahmetkaan.uygulamakapatma.ui.permission

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.AccessibilityNew
import androidx.compose.material.icons.outlined.BatteryChargingFull
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ahmetkaan.uygulamakapatma.ui.theme.AppColors
import com.ahmetkaan.uygulamakapatma.util.DeviceBrandHelper
import com.ahmetkaan.uygulamakapatma.util.PermissionLauncher

/**
 * 2027 İzin & Güvenlik Merkezi (Permission Hub).
 * Kontrol listesi yaklaşımı, görsel ayar simülasyonu ve Android 13+ kısıtlanmış ayar çözümü.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionScreen(
    viewModel: PermissionViewModel = viewModel(),
    onAllPermissionsGranted: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scrollState = rememberScrollState()

    var activeDetailPermission by remember { mutableStateOf<PermissionType?>(null) }
    var showRestrictedGuide by remember { mutableStateOf(false) }

    // Yaşam döngüsünde izinleri canlı yenile
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshPermissions()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Üst Başlık ve İlerleme Kartı
                Column(modifier = Modifier.padding(top = 20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AppColors.PrimaryIndigo.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Security,
                                    contentDescription = null,
                                    tint = AppColors.CyberCyan,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "İzin & Güvenlik Merkezi",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.TextPrimary
                                )
                                Text(
                                    text = "3 Temel Koruma İzni",
                                    fontSize = 12.sp,
                                    color = AppColors.TextSecondary
                                )
                            }
                        }

                        // Durum Rozeti
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (uiState.allCoreGranted) AppColors.EmeraldSuccess.copy(alpha = 0.15f)
                                    else AppColors.PrimaryIndigo.copy(alpha = 0.15f)
                                )
                                .border(
                                    1.dp,
                                    if (uiState.allCoreGranted) AppColors.EmeraldSuccess else AppColors.PrimaryIndigoLight,
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (uiState.allCoreGranted) "Hazır ✓" else "${uiState.grantedCount}/3 Tamam",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.allCoreGranted) AppColors.EmeraldSuccess else AppColors.CyberCyan
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // İlerleme Çubuğu
                    val animatedProgress by animateFloatAsState(
                        targetValue = uiState.progress,
                        label = "permProgress"
                    )

                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (uiState.allCoreGranted) AppColors.EmeraldSuccess else AppColors.PrimaryIndigo,
                        trackColor = Color(0x22818CF8),
                        strokeCap = StrokeCap.Round
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Android 13+ APK Uyarısı (Eğer gerekiyorsa proaktif sarı rozet)
                    if (uiState.needsRestrictedUnlock && !uiState.accessibilityGranted) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(AppColors.AmberWarning.copy(alpha = 0.12f))
                                .border(1.dp, AppColors.AmberWarning.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                                .clickable { showRestrictedGuide = true }
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("💡", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Android 13+ Kısıtlanmış Ayar Kilidi",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AppColors.AmberWarning
                                    )
                                    Text(
                                        text = "Erişilebilirlik anahtarı gri mi? 30 saniyede açmak için buraya dokunun.",
                                        fontSize = 11.sp,
                                        color = Color(0xFFFFD699),
                                        lineHeight = 15.sp
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Outlined.ChevronRight,
                                    contentDescription = null,
                                    tint = AppColors.AmberWarning,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // İzin Kartları Listesi
                    Text(
                        text = "ZORUNLU KORUMA İZİNLERİ",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextSecondary,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 1. Kullanım Sürelerini Ölçme
                    PermissionCheckCard(
                        icon = Icons.Outlined.QueryStats,
                        title = "Kullanım Süresini Ölçme",
                        desc = "Hangi uygulamanın kaç dakika kullanıldığını ölçer",
                        isGranted = uiState.usageStatsGranted,
                        isRequired = true,
                        onClick = {
                            activeDetailPermission = PermissionType.USAGE
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2. Korumayı Anında Devreye Alma (Accessibility)
                    PermissionCheckCard(
                        icon = Icons.Outlined.AccessibilityNew,
                        title = "Uygulamayı Anında Yakalama",
                        desc = "Süresi dolan uygulama açıldığında fark eder",
                        isGranted = uiState.accessibilityGranted,
                        isRequired = true,
                        onClick = {
                            activeDetailPermission = PermissionType.ACCESSIBILITY
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 3. Kilit Ekranını Gösterme (Overlay)
                    PermissionCheckCard(
                        icon = Icons.Outlined.Layers,
                        title = "Kilit Ekranını Üstte Gösterme",
                        desc = "Süre bittiğinde 'Süre Doldu' uyarısını açar",
                        isGranted = uiState.overlayGranted,
                        isRequired = true,
                        onClick = {
                            activeDetailPermission = PermissionType.OVERLAY
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "ÖNERİLEN İZİNLER (İSTEĞE BAĞLI)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextSecondary,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 4. Pil Optimizasyonu
                    PermissionCheckCard(
                        icon = Icons.Outlined.BatteryChargingFull,
                        title = "Arka Planda Kesintisiz Çalışma",
                        desc = "Telefonun pil koruması servisi durdurmasın",
                        isGranted = uiState.batteryOptIgnored,
                        isRequired = false,
                        onClick = {
                            activeDetailPermission = PermissionType.BATTERY
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 5. Bildirim İzni
                    PermissionCheckCard(
                        icon = Icons.Outlined.NotificationsActive,
                        title = "Süre Hatırlatma Bildirimleri",
                        desc = "'5 dakika kaldı' gibi uyarılar gönderir",
                        isGranted = uiState.notificationGranted,
                        isRequired = false,
                        onClick = {
                            activeDetailPermission = PermissionType.NOTIFICATION
                        }
                    )
                }

                // Alt Buton (Devam Et)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                ) {
                    Button(
                        onClick = {
                            if (uiState.allCoreGranted) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onAllPermissionsGranted()
                            }
                        },
                        enabled = uiState.allCoreGranted,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.EmeraldSuccess,
                            disabledContainerColor = Color(0xFF222638)
                        )
                    ) {
                        Text(
                            text = if (uiState.allCoreGranted) "Kuruluma Devam Et" else "3 Zorunlu İzni Tamamlayın (${uiState.grantedCount}/3)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.allCoreGranted) Color.White else AppColors.TextTertiary
                        )
                        if (uiState.allCoreGranted) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Başarı Animasyon Katmanı (İzin verildiğinde otomatik patlar)
            PermissionSuccessOverlay(
                visible = uiState.justGrantedPermission != null,
                onDismiss = { viewModel.clearJustGranted() }
            )
        }
    }

    // Odaklanmış İzin Walkthrough Sayfası (ModalBottomSheet)
    activeDetailPermission?.let { permType ->
        PermissionDetailSheet(
            permissionType = permType,
            onDismiss = { activeDetailPermission = null },
            onOpenSettings = {
                activeDetailPermission = null
                when (permType) {
                    PermissionType.USAGE -> PermissionLauncher.openUsageStatsSettings(context)
                    PermissionType.ACCESSIBILITY -> PermissionLauncher.openAccessibilitySettings(context)
                    PermissionType.OVERLAY -> PermissionLauncher.openOverlaySettings(context)
                    PermissionType.BATTERY -> PermissionLauncher.openBatteryOptimizationSettings(context)
                    PermissionType.NOTIFICATION -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            PermissionLauncher.openAppDetails(context)
                        }
                    }
                }
            },
            onOpenRestrictedGuide = {
                activeDetailPermission = null
                showRestrictedGuide = true
            }
        )
    }

    // Kısıtlanmış Ayar Rehberi
    if (showRestrictedGuide) {
        RestrictedSettingsGuideDialog(onDismiss = { showRestrictedGuide = false })
    }
}

/**
 * Kontrol Listesi İzin Kartı
 */
@Composable
private fun PermissionCheckCard(
    icon: ImageVector,
    title: String,
    desc: String,
    isGranted: Boolean,
    isRequired: Boolean,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isGranted) AppColors.EmeraldSuccess.copy(alpha = 0.5f) else AppColors.GlassBorder,
        label = "permBorder"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isGranted) Color(0xFF0F1A17) else AppColors.GlassSurface)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        if (isGranted) AppColors.EmeraldSuccess.copy(alpha = 0.15f)
                        else AppColors.PrimaryIndigo.copy(alpha = 0.12f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isGranted) Icons.Outlined.Check else icon,
                    contentDescription = null,
                    tint = if (isGranted) AppColors.EmeraldSuccess else AppColors.CyberCyan,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                    if (isRequired && !isGranted) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(AppColors.CrimsonAlert.copy(alpha = 0.2f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "ZORUNLU",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.CrimsonAlert
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = desc,
                    fontSize = 12.sp,
                    color = AppColors.TextSecondary,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (isGranted) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppColors.EmeraldSuccess.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Aktif ✓",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.EmeraldSuccess
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppColors.PrimaryIndigo.copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Ayarla >",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.CyberCyan
                    )
                }
            }
        }
    }
}

/**
 * Tek Bir İznin Görsel Walkthrough Paneli (ModalBottomSheet)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PermissionDetailSheet(
    permissionType: PermissionType,
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenRestrictedGuide: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val title = when (permissionType) {
        PermissionType.USAGE -> "Kullanım Süresini Ölçme"
        PermissionType.ACCESSIBILITY -> "Uygulamayı Anında Yakalama"
        PermissionType.OVERLAY -> "Kilit Ekranını Üstte Gösterme"
        PermissionType.BATTERY -> "Kesintisiz Arka Plan İzni"
        PermissionType.NOTIFICATION -> "Süre Uyarı Bildirimleri"
    }

    val why = when (permissionType) {
        PermissionType.USAGE -> "Çocuğunuzun hangi uygulamada kaç dakika vakit geçirdiğini doğru hesaplamak için gereklidir."
        PermissionType.ACCESSIBILITY -> "Belirlediğiniz süre dolduğunda, uygulamanın açıldığını 0.1 saniyede algılayıp kilit ekranını getirmek için gereklidir."
        PermissionType.OVERLAY -> "Süre dolduğunda oyunun veya uygulamanın üzerine 'Süre Doldu' kalkanını açabilmek için gereklidir."
        PermissionType.BATTERY -> "Telefonun ekranı kapalıyken Android pil tasarrufu sisteminin koruma servisini durdurmasını engeller."
        PermissionType.NOTIFICATION -> "Limit dolmadan 5 dakika önce çocuğunuza nazik uyarı bildirimi göndermek için gereklidir."
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AppColors.GlassSurfaceElevated,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(AppColors.GlassBorder)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = why,
                fontSize = 13.sp,
                color = AppColors.TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Görsel Ayar Ekranı Mockup'ı
            SettingsToggleMockup(
                appName = "Ebeveyn Denetimi",
                subtitle = "Anahtarı açık konuma getirin",
                highlightColor = AppColors.CyberCyan
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Android 13+ Kısıtlanmış Ayar Kısayolu (Sadece Erişilebilirlikte)
            if (permissionType == PermissionType.ACCESSIBILITY) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x1AF59E0B))
                        .clickable(onClick = onOpenRestrictedGuide)
                        .padding(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Ayar anahtarı gri renkte mi? Rehberi aç",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.AmberWarning
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Google Play Zorunlu Ön Bilgilendirme Kartı (Prominent Disclosure - Asla gizlenemez)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(AppColors.CyberCyan.copy(alpha = 0.08f))
                    .border(1.dp, AppColors.CyberCyan.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Shield,
                            contentDescription = null,
                            tint = AppColors.CyberCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (permissionType == PermissionType.ACCESSIBILITY) {
                                androidx.compose.ui.res.stringResource(com.ahmetkaan.uygulamakapatma.R.string.disclosure_accessibility_title)
                            } else {
                                androidx.compose.ui.res.stringResource(com.ahmetkaan.uygulamakapatma.R.string.disclosure_usage_title)
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.CyberCyan
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (permissionType == PermissionType.ACCESSIBILITY) {
                            androidx.compose.ui.res.stringResource(com.ahmetkaan.uygulamakapatma.R.string.disclosure_accessibility_message)
                        } else {
                            androidx.compose.ui.res.stringResource(com.ahmetkaan.uygulamakapatma.R.string.disclosure_usage_message)
                        },
                        fontSize = 11.sp,
                        color = AppColors.TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Açık Rıza ve Ayarları Aç Butonu (Google Play Explicit Consent)
            Button(
                onClick = onOpenSettings,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryIndigo)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = androidx.compose.ui.res.stringResource(com.ahmetkaan.uygulamakapatma.R.string.disclosure_accept),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = androidx.compose.ui.res.stringResource(com.ahmetkaan.uygulamakapatma.R.string.disclosure_deny),
                    fontSize = 13.sp,
                    color = AppColors.TextTertiary
                )
            }
        }
    }
}
