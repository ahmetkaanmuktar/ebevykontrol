package com.ahmetkaan.uygulamakapatma.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ElectricBolt
import androidx.compose.material.icons.outlined.HourglassBottom
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ahmetkaan.uygulamakapatma.data.repository.ParentSettingsRepository
import com.ahmetkaan.uygulamakapatma.ui.pin.PinVerificationDialog
import com.ahmetkaan.uygulamakapatma.ui.theme.AppColors
import com.ahmetkaan.uygulamakapatma.util.AppInfo

/**
 * 2027+ Fütüristik Ebeveyn Kontrol Paneli:
 * - Bento Grid Hero Dashboard (Neon Arc Gauge, Akıllı Sayaçlar, Acil Odak Modu)
 * - Akıllı Kategori Çipleri (Tümü, Kısıtlananlar, Süresi Dolanlar, Kilitliler, En Çok Kullanılanlar)
 * - Cyber-Glassmorphism Uygulama Kartları
 * - Yeni Nesil ModalBottomSheet Limit Düzenleyici
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    parentRepo: ParentSettingsRepository,
    viewModel: DashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current

    var selectedAppForLimit by remember { mutableStateOf<AppInfo?>(null) }
    var pendingActionWithPin by remember { mutableStateOf<(() -> Unit)?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AppColors.PrimaryGradient)
                                .padding(1.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(11.dp))
                                    .background(AppColors.GlassSurface),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Shield,
                                    contentDescription = null,
                                    tint = AppColors.CyberCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Ebeveyn Kalkanı",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = AppColors.TextPrimary
                            )
                            Text(
                                text = "Akıllı Dijital Denge",
                                fontSize = 11.sp,
                                color = AppColors.TextSecondary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.loadData()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = "Yenile",
                            tint = AppColors.CyberCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.BackgroundDark.copy(alpha = 0.95f),
                    titleContentColor = AppColors.TextPrimary
                )
            )
        },
        containerColor = AppColors.BackgroundDark
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = AppColors.PrimaryIndigo,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Uygulama analiz ediliyor...",
                        fontSize = 13.sp,
                        color = AppColors.TextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Bento Grid Hero Bölümü (Dairesel Gösterge + Metrikler + Acil Odak)
                item {
                    BentoHeroSection(
                        totalMinutes = uiState.totalScreenTimeMinutes,
                        restrictedCount = uiState.restrictedAppsCount,
                        exceededCount = uiState.exceededAppsCount,
                        topAppName = uiState.topAppName,
                        topAppMinutes = uiState.topAppMinutes,
                        isInstantLockdownActive = uiState.isInstantLockdownActive,
                        onToggleInstantLockdown = { enable ->
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            pendingActionWithPin = {
                                viewModel.toggleInstantLockdown(enable)
                            }
                        }
                    )
                }

                // 2. Arama Çubuğu (Glassmorphic)
                item {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = {
                            Text(
                                "Uygulama veya paket ara...",
                                color = AppColors.TextTertiary,
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = null,
                                tint = AppColors.CyberCyan
                            )
                        },
                        trailingIcon = {
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Clear,
                                        contentDescription = "Temizle",
                                        tint = AppColors.TextSecondary
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = AppColors.TextPrimary,
                            unfocusedTextColor = AppColors.TextPrimary,
                            focusedContainerColor = AppColors.GlassSurface,
                            unfocusedContainerColor = AppColors.GlassSurface,
                            focusedBorderColor = AppColors.PrimaryIndigo,
                            unfocusedBorderColor = AppColors.GlassBorder
                        )
                    )
                }

                // 3. Akıllı Filtreleme Çipleri
                item {
                    FilterChipsRow(
                        selectedFilter = uiState.activeFilter,
                        onSelectFilter = { filter ->
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.setFilter(filter)
                        }
                    )
                }

                // 4. Liste Başlığı ve Sayaç
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${uiState.activeFilter.label} (${uiState.filteredApps.size})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.TextSecondary
                        )

                        Text(
                            text = "Dokun: Limit Belirle",
                            fontSize = 11.sp,
                            color = AppColors.TextTertiary
                        )
                    }
                }

                // 5. Modern Uygulama Kartları Listesi veya Boş Durum (Empty State)
                if (uiState.filteredApps.isEmpty()) {
                    item {
                        DashboardEmptyStateCard(
                            searchQuery = uiState.searchQuery,
                            activeFilter = uiState.activeFilter,
                            onClearSearch = { viewModel.setSearchQuery("") },
                            onResetFilter = { viewModel.setFilter(AppFilter.ALL) }
                        )
                    }
                } else {
                    items(uiState.filteredApps, key = { it.packageName }) { appInfo ->
                        ModernAppCard(
                            appInfo = appInfo,
                            onConfigureLimit = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                pendingActionWithPin = {
                                    selectedAppForLimit = appInfo
                                }
                            },
                            onToggleBlock = { isBlocked ->
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                pendingActionWithPin = {
                                    viewModel.togglePermanentBlock(appInfo.packageName, isBlocked)
                                }
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }

    // Modern ModalBottomSheet Limit Ayarlama
    if (selectedAppForLimit != null) {
        ModernLimitBottomSheet(
            appInfo = selectedAppForLimit!!,
            onDismiss = { selectedAppForLimit = null },
            onSaveLimit = { minutes ->
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.setAppLimit(selectedAppForLimit!!.packageName, minutes)
                selectedAppForLimit = null
            },
            onRemoveLimit = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.removeLimit(selectedAppForLimit!!.packageName)
                selectedAppForLimit = null
            }
        )
    }

    // Güvenlik: PIN Doğrulama
    if (pendingActionWithPin != null) {
        PinVerificationDialog(
            parentRepo = parentRepo,
            onSuccess = {
                val action = pendingActionWithPin
                pendingActionWithPin = null
                action?.invoke()
            },
            onDismiss = {
                pendingActionWithPin = null
            }
        )
    }
}

/**
 * 2027 Bento Grid Hero Gösterge Paneli
 */
@Composable
private fun BentoHeroSection(
    totalMinutes: Int,
    restrictedCount: Int,
    exceededCount: Int,
    topAppName: String,
    topAppMinutes: Int,
    isInstantLockdownActive: Boolean,
    onToggleInstantLockdown: (Boolean) -> Unit
) {
    val hours = totalMinutes / 60
    val mins = totalMinutes % 60
    val formattedTotal = if (hours > 0) "${hours}s ${mins}dk" else "${mins}dk"

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Üst Ana Bento Kartı (Dairesel Arc Gauge ve Özet)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(AppColors.CardGradient)
                .border(1.dp, AppColors.GlassBorder, RoundedCornerShape(22.dp))
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Sol Metinler
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(AppColors.EmeraldSuccess)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CANLI İZLEME AKTİF",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.EmeraldSuccess,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = formattedTotal,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AppColors.TextPrimary
                    )

                    Text(
                        text = "Bugünkü Toplam Ekran Süresi",
                        fontSize = 12.sp,
                        color = AppColors.TextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatBadge(
                            label = "$restrictedCount Kural",
                            color = AppColors.PrimaryIndigo
                        )
                        if (exceededCount > 0) {
                            StatBadge(
                                label = "$exceededCount Limit Doldu",
                                color = AppColors.CrimsonAlert
                            )
                        }
                    }
                }

                // Sağ: Neon Arc Gauge (Canvas Çizimi)
                Box(
                    modifier = Modifier.size(90.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val progressAnim by animateFloatAsState(
                        targetValue = (totalMinutes.toFloat() / 360f).coerceIn(0.05f, 1f),
                        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                        label = "gauge"
                    )

                    Canvas(modifier = Modifier.size(80.dp)) {
                        val strokeWidth = 8.dp.toPx()
                        // Arka plan dairesi
                        drawArc(
                            color = Color(0x22818CF8),
                            startAngle = 140f,
                            sweepAngle = 260f,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                        // Dolgu yayı (Gradient)
                        drawArc(
                            brush = AppColors.PrimaryGradient,
                            startAngle = 140f,
                            sweepAngle = 260f * progressAnim,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    Icon(
                        imageVector = Icons.Outlined.Timer,
                        contentDescription = null,
                        tint = AppColors.CyberCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // İkinci Satır: Acil Aile / Odak Kilidi Bento Kartı
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(
                    if (isInstantLockdownActive) AppColors.CrimsonAlertSoft else AppColors.GlassSurface
                )
                .border(
                    1.dp,
                    if (isInstantLockdownActive) AppColors.CrimsonAlert else AppColors.GlassBorder,
                    RoundedCornerShape(18.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (isInstantLockdownActive) AppColors.CrimsonAlert else Color(0x266366F1)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isInstantLockdownActive) Icons.Outlined.Lock else Icons.Outlined.LockOpen,
                            contentDescription = null,
                            tint = if (isInstantLockdownActive) Color.White else AppColors.PrimaryIndigoLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = if (isInstantLockdownActive) "Acil Kilit Aktif" else "Hızlı Aile / Odak Kilidi",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isInstantLockdownActive) AppColors.CrimsonAlert else AppColors.TextPrimary
                        )
                        Text(
                            text = if (isInstantLockdownActive) "Kısıtlanan tüm uygulamalar şu an kilitli" else "Ders / Uyku için tüm kısıtlıları anında kilitle",
                            fontSize = 11.sp,
                            color = AppColors.TextSecondary
                        )
                    }
                }

                Switch(
                    checked = isInstantLockdownActive,
                    onCheckedChange = onToggleInstantLockdown,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AppColors.CrimsonAlert,
                        uncheckedThumbColor = AppColors.TextSecondary,
                        uncheckedTrackColor = Color(0x334E557A)
                    )
                )
            }
        }

        // Üçüncü Satır (Opsiyonel En Çok Vakit Geçirilen Uygulama)
        if (topAppName.isNotBlank() && topAppMinutes > 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(AppColors.GlassSurface)
                    .border(1.dp, AppColors.GlassBorder, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.HourglassBottom,
                            contentDescription = null,
                            tint = AppColors.AmberWarning,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "En çok kullanılan: $topAppName",
                            fontSize = 12.sp,
                            color = AppColors.TextSecondary
                        )
                    }
                    Text(
                        text = "$topAppMinutes dk",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.AmberWarning
                    )
                }
            }
        }
    }
}

@Composable
private fun StatBadge(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

/**
 * Filtreleme Çipleri Sırası
 */
@Composable
private fun FilterChipsRow(
    selectedFilter: AppFilter,
    onSelectFilter: (AppFilter) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
        items(AppFilter.values()) { filter ->
            val isSelected = filter == selectedFilter

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) AppColors.PrimaryIndigo else AppColors.GlassSurface
                    )
                    .border(
                        1.dp,
                        if (isSelected) AppColors.PrimaryIndigoLight else AppColors.GlassBorder,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { onSelectFilter(filter) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = filter.label,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else AppColors.TextSecondary
                )
            }
        }
    }
}

/**
 * 2027 Cyber-Glass Uygulama Kartı
 */
@Composable
private fun ModernAppCard(
    appInfo: AppInfo,
    onConfigureLimit: () -> Unit,
    onToggleBlock: (Boolean) -> Unit
) {
    val limit = appInfo.limit
    val isPermanentlyBlocked = limit?.isPermanentlyBlocked == true
    val hasLimit = limit != null && limit.dailyLimitMinutes > 0
    val limitMinutes = limit?.dailyLimitMinutes ?: 0
    val usedMinutes = appInfo.todayUsageMinutes
    val isExceeded = hasLimit && usedMinutes >= limitMinutes

    val cardBorderColor by animateColorAsState(
        targetValue = when {
            isPermanentlyBlocked || isExceeded -> AppColors.CrimsonAlert.copy(alpha = 0.5f)
            hasLimit -> AppColors.PrimaryIndigo.copy(alpha = 0.4f)
            else -> AppColors.GlassBorder
        },
        label = "border"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                if (isPermanentlyBlocked || isExceeded) Color(0xFF1E1318) else AppColors.GlassSurface
            )
            .border(1.dp, cardBorderColor, RoundedCornerShape(18.dp))
            .clickable { onConfigureLimit() }
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Uygulama Simgesi
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = appInfo.icon.asImageBitmap(),
                        contentDescription = appInfo.appName,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // İsim & Durum Rozeti
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = appInfo.appName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Bugün: $usedMinutes dk",
                            fontSize = 12.sp,
                            color = AppColors.TextSecondary
                        )

                        if (isPermanentlyBlocked) {
                            Spacer(modifier = Modifier.width(8.dp))
                            BadgePill(text = "KİLİTLİ", color = AppColors.CrimsonAlert)
                        } else if (isExceeded) {
                            Spacer(modifier = Modifier.width(8.dp))
                            BadgePill(text = "SÜRE DOLDU", color = AppColors.CrimsonAlert)
                        } else if (hasLimit) {
                            Spacer(modifier = Modifier.width(8.dp))
                            val rem = (limitMinutes - usedMinutes).coerceAtLeast(0)
                            BadgePill(text = "$rem dk kaldı", color = AppColors.CyberCyan)
                        }
                    }
                }

                // Hızlı Kilit Aç/Kapa Switch
                Switch(
                    checked = isPermanentlyBlocked,
                    onCheckedChange = onToggleBlock,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AppColors.CrimsonAlert,
                        uncheckedThumbColor = AppColors.TextSecondary,
                        uncheckedTrackColor = Color(0x334E557A)
                    )
                )
            }

            // Süre Limiti ve İlerleme Çubuğu
            if (hasLimit && !isPermanentlyBlocked) {
                Spacer(modifier = Modifier.height(12.dp))
                val progress = (usedMinutes.toFloat() / limitMinutes.toFloat()).coerceIn(0f, 1f)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Limit: $limitMinutes dk",
                        fontSize = 11.sp,
                        color = AppColors.TextSecondary
                    )
                    Text(
                        text = "%${(progress * 100).toInt()}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isExceeded) AppColors.CrimsonAlert else AppColors.CyberCyan
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (isExceeded) AppColors.CrimsonAlert else AppColors.PrimaryIndigo,
                    trackColor = Color(0x22818CF8),
                    strokeCap = StrokeCap.Round
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Aksiyon Butonu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x1A818CF8))
                        .clickable { onConfigureLimit() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = null,
                            tint = AppColors.PrimaryIndigoLight,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (hasLimit) "Limiti Güncelle" else "Limit Tanımla",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.PrimaryIndigoLight
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BadgePill(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

/**
 * 2027 Yeni Nesil ModalBottomSheet Limit Ayarlayıcı
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernLimitBottomSheet(
    appInfo: AppInfo,
    onDismiss: () -> Unit,
    onSaveLimit: (Int) -> Unit,
    onRemoveLimit: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val initialLimit = appInfo.limit?.dailyLimitMinutes ?: 45
    var sliderValue by remember { mutableFloatStateOf(initialLimit.coerceAtLeast(15).toFloat()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AppColors.GlassSurfaceElevated,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(42.dp)
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
                .padding(bottom = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Başlık ve Uygulama Bilgisi
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    bitmap = appInfo.icon.asImageBitmap(),
                    contentDescription = appInfo.appName,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = appInfo.appName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                    Text(
                        text = "Günlük Ekran Süresi Limiti",
                        fontSize = 12.sp,
                        color = AppColors.TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Seçili Süre Göstergesi
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x266366F1))
                    .border(1.dp, AppColors.GlassBorderActive, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val minutesInt = sliderValue.toInt()
                    val h = minutesInt / 60
                    val m = minutesInt % 60
                    val displayTime = if (h > 0) "${h} Saat ${m} Dakika" else "$m Dakika"

                    Text(
                        text = displayTime,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AppColors.CyberCyan
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Günde en fazla bu kadar kullanılabilir",
                        fontSize = 12.sp,
                        color = AppColors.TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Hızlı Ön Ayar Butonları
            Text(
                text = "Hızlı Seçim",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextSecondary,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(15, 30, 45, 60, 90, 120).forEach { presetMinutes ->
                    val isSelected = sliderValue.toInt() == presetMinutes
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) AppColors.PrimaryIndigo else AppColors.GlassSurface)
                            .border(
                                1.dp,
                                if (isSelected) AppColors.CyberCyan else AppColors.GlassBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { sliderValue = presetMinutes.toFloat() }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (presetMinutes >= 60) "${presetMinutes / 60}s" else "${presetMinutes}d",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else AppColors.TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Hassas Kaydırıcı (Slider)
            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                valueRange = 15f..360f,
                steps = 22, // 15'er dk adımlarla 6 saate kadar
                colors = SliderDefaults.colors(
                    thumbColor = AppColors.CyberCyan,
                    activeTrackColor = AppColors.PrimaryIndigo,
                    inactiveTrackColor = Color(0x334E557A)
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("15 dk", fontSize = 11.sp, color = AppColors.TextTertiary)
                Text("2 saat", fontSize = 11.sp, color = AppColors.TextTertiary)
                Text("4 saat", fontSize = 11.sp, color = AppColors.TextTertiary)
                Text("6 saat", fontSize = 11.sp, color = AppColors.TextTertiary)
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Kaydet Butonu
            Button(
                onClick = { onSaveLimit(sliderValue.toInt()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryIndigo)
            ) {
                Text(
                    text = "Limiti Kaydet ve Uygula",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // İptal veya Limiti Kaldır Butonları
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (appInfo.limit != null) {
                    TextButton(onClick = onRemoveLimit) {
                        Text(
                            text = "Limiti Tamamen Kaldır",
                            color = AppColors.CrimsonAlert,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Vazgeç",
                        color = AppColors.TextSecondary,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

/**
 * 2027 Arama & Filtreleme Boş Durum (Empty State) Kartı
 */
@Composable
private fun DashboardEmptyStateCard(
    searchQuery: String,
    activeFilter: AppFilter,
    onClearSearch: () -> Unit,
    onResetFilter: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(AppColors.GlassSurface)
            .border(1.dp, AppColors.GlassBorder, RoundedCornerShape(20.dp))
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(AppColors.PrimaryIndigo.copy(alpha = 0.12f))
                    .border(1.dp, AppColors.GlassBorderActive, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = AppColors.CyberCyan,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val title = if (searchQuery.isNotBlank()) {
                "\"$searchQuery\" ile Eşleşen Uygulama Yok"
            } else {
                "${activeFilter.label} Kategorisinde Uygulama Yok"
            }

            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            val desc = if (searchQuery.isNotBlank()) {
                "Farklı bir kelime aramayı deneyebilir veya arama filtresini temizleyebilirsiniz."
            } else {
                "Bu kategoriye ait herhangi bir kural veya uygulama bulunmuyor."
            }

            Text(
                text = desc,
                fontSize = 12.sp,
                color = AppColors.TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (searchQuery.isNotBlank()) {
                OutlinedButton(
                    onClick = onClearSearch,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.CyberCyan.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "Aramayı Temizle",
                        color = AppColors.CyberCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else if (activeFilter != AppFilter.ALL) {
                OutlinedButton(
                    onClick = onResetFilter,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.PrimaryIndigoLight)
                ) {
                    Text(
                        text = "Tüm Uygulamaları Göster",
                        color = AppColors.PrimaryIndigoLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
