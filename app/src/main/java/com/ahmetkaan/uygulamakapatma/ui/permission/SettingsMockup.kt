package com.ahmetkaan.uygulamakapatma.ui.permission

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ahmetkaan.uygulamakapatma.ui.theme.AppColors

/**
 * Android Ayarlar Ekranını Taklit Eden Canlı Görsel Mockup.
 * Kullanıcı ayarlara gittiğinde ne arayacağını ve hangi anahtara basacağını görsel olarak gösterir.
 */
@Composable
fun SettingsToggleMockup(
    appName: String = "Ebeveyn Denetimi",
    subtitle: String = "İzni etkinleştirin",
    highlightColor: Color = AppColors.CyberCyan
) {
    val infiniteTransition = rememberInfiniteTransition(label = "tapAnimation")

    val fingerScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fingerScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F111A))
            .border(1.dp, AppColors.GlassBorderActive, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column {
            // Sahte Sistem Çubuğu
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                )
                Text(
                    text = "AYARLAR ÖNİZLEMESİ",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = highlightColor,
                    letterSpacing = 1.sp
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(AppColors.EmeraldSuccess)
                )
            }

            // Simüle Edilmiş Ayar Satırı
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF191C2B))
                    .border(1.dp, highlightColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
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
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AppColors.PrimaryIndigo.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Security,
                                contentDescription = null,
                                tint = AppColors.CyberCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = appName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = subtitle,
                                fontSize = 11.sp,
                                color = AppColors.TextSecondary
                            )
                        }
                    }

                    // Anahtar ve Üzerinde Dokunma İkonu
                    Box(contentAlignment = Alignment.Center) {
                        Switch(
                            checked = true,
                            onCheckedChange = null,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = highlightColor,
                                uncheckedTrackColor = Color(0x334E557A)
                            ),
                            modifier = Modifier.scale(0.85f)
                        )

                        // Canlı Dokunma (Touch) İndikatörü
                        Icon(
                            imageVector = Icons.Outlined.TouchApp,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .offset(x = 10.dp, y = 14.dp)
                                .scale(fingerScale)
                                .size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "💡 Ayarlar açılınca listeden \"$appName\" seçip anahtarı AÇIN.",
                fontSize = 11.sp,
                color = AppColors.TextSecondary,
                lineHeight = 15.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

/**
 * Android 13+ Kısıtlanmış Ayar (⋮ 3 Nokta) Ekran Mockup'ı
 */
@Composable
fun RestrictedSettingsMockup() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulseMenu")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F111A))
            .border(1.dp, AppColors.AmberWarning.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column {
            // Android Başlık Çubuğu Mockup'ı
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1E2133))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Uygulama Bilgisi",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                // Vurgulanan 3 Nokta
                Box(
                    modifier = Modifier
                        .scale(pulse)
                        .clip(CircleShape)
                        .background(AppColors.AmberWarning.copy(alpha = 0.25f))
                        .border(1.dp, AppColors.AmberWarning, CircleShape)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = "3 Nokta Menüsü",
                        tint = AppColors.AmberWarning,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Açılan Menü Önizlemesi
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF282C44))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "1. Sağ üstteki (⋮) 3 noktaya dokunun",
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "2. \"Kısıtlanmış ayarlara izin ver\" seçin",
                        fontSize = 12.sp,
                        color = AppColors.CyberCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "3. Telefon kilit şifrenizi girin",
                        fontSize = 12.sp,
                        color = AppColors.TextSecondary
                    )
                }
            }
        }
    }
}
