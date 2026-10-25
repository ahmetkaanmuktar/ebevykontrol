package com.ahmetkaan.uygulamakapatma.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

enum class BrandType {
    XIAOMI,
    SAMSUNG,
    HUAWEI,
    OPPO,
    VIVO,
    STOCK_ANDROID
}

data class BrandGuideInfo(
    val brandName: String,
    val title: String,
    val instructions: List<String>,
    val tip: String
)

/**
 * Samsung, Xiaomi, Huawei, OPPO, Vivo ve saf Android cihazların özel arka plan
 * ve pil yönetim ekranlarını tespit edip doğrudan açan yardımcı sınıf.
 */
object DeviceBrandHelper {

    fun getDeviceBrand(): BrandType {
        val manufacturer = Build.MANUFACTURER.lowercase()
        return when {
            manufacturer.contains("xiaomi") || manufacturer.contains("redmi") || manufacturer.contains("poco") -> BrandType.XIAOMI
            manufacturer.contains("samsung") -> BrandType.SAMSUNG
            manufacturer.contains("huawei") || manufacturer.contains("honor") -> BrandType.HUAWEI
            manufacturer.contains("oppo") || manufacturer.contains("realme") || manufacturer.contains("oneplus") -> BrandType.OPPO
            manufacturer.contains("vivo") || manufacturer.contains("iqoo") -> BrandType.VIVO
            else -> BrandType.STOCK_ANDROID
        }
    }

    fun getBrandGuide(brand: BrandType): BrandGuideInfo {
        return when (brand) {
            BrandType.XIAOMI -> BrandGuideInfo(
                brandName = "Xiaomi / Redmi / POCO",
                title = "Xiaomi Arka Plan Koruması",
                instructions = listOf(
                    "\"Otomatik Başlatmayı Aç\" butonuna basıp uygulamayı aktif edin.",
                    "\"Xiaomi Ek İzinleri\" butonuna basıp \"Arka planda açılır pencere göster\" iznini verin.",
                    "\"Pil Kısıtlamasını Kaldır\" seçeneğinde \"Kısıtlama Yok\"u seçin."
                ),
                tip = "Son Uygulamalar ekranında Ebeveyn Denetimi kartını aşağı çekip kilit simgesine dokunarak bellekte kilitleyebilirsiniz."
            )
            BrandType.SAMSUNG -> BrandGuideInfo(
                brandName = "Samsung (One UI)",
                title = "Samsung Derin Uyku Koruması",
                instructions = listOf(
                    "\"Pil Kısıtlamasını Kaldır\" butonuna dokunup \"İzin Ver\"i seçin.",
                    "Telefonunuzda: Ayarlar → Pil → Arka plan kullanım sınırları bölümüne gidin.",
                    "\"Asla otomatik uyku moduna alınmayan uygulamalar\" listesine Ebeveyn Denetimi'ni ekleyin."
                ),
                tip = "Samsung, 3 gün açılmayan uygulamaları 'Derin Uyku'ya alabilir. Bu ayar sınırların aksamasını önler."
            )
            BrandType.HUAWEI -> BrandGuideInfo(
                brandName = "Huawei / Honor",
                title = "Huawei Başlatma Koruması",
                instructions = listOf(
                    "\"Uygulama Başlatma Ayarları\" butonuna dokunun.",
                    "Ebeveyn Denetimi'ni bulun ve \"Otomatik Yönet\" anahtarını kapatın.",
                    "Açılan pencerede: Otomatik Başlatma, İkincil Başlatma ve Arka Planda Çalışma izinlerinin üçünü de AÇIK bırakın."
                ),
                tip = "Huawei PowerGenie ekran kapandığında servisleri durdurabilir. Manuel yönetim bunu engeller."
            )
            BrandType.OPPO -> BrandGuideInfo(
                brandName = "OPPO / Realme / OnePlus",
                title = "ColorOS Arka Plan İzni",
                instructions = listOf(
                    "\"Pil Ayarlarını Aç\" butonuna dokunun.",
                    "\"Arka plan etkinliğine izin ver\" ve \"Otomatik başlatmaya izin ver\" seçeneklerini açın.",
                    "Son Uygulamalar ekranında uygulamayı aşağı çekip kilit simgesine dokunun."
                ),
                tip = "Temizleme yapıldığında servisin durdurulmaması için kilit simgesi eklemeniz önerilir."
            )
            BrandType.VIVO -> BrandGuideInfo(
                brandName = "Vivo / iQOO",
                title = "Vivo Yüksek Güç İzni",
                instructions = listOf(
                    "\"Pil Ayarlarını Aç\" butonuna dokunun.",
                    "\"Yüksek arka plan güç tüketimi\" menüsüne girin.",
                    "Ebeveyn Denetimi uygulamasının arka planda çalışmasına izin verin."
                ),
                tip = "Vivo telefonlarda iManager arka plan uygulamalarını kısıtlayabilir. Bu ayar sürekliliği sağlar."
            )
            BrandType.STOCK_ANDROID -> BrandGuideInfo(
                brandName = "Android",
                title = "Kesintisiz Arka Plan İzni",
                instructions = listOf(
                    "\"Pil Kısıtlamasını Kaldır\" butonuna dokunun.",
                    "Açılan sistem bildiriminde \"İzin Ver\" seçeneğini işaretleyin."
                ),
                tip = "Bu sayede Android Doze modu servisin ekran kapalıyken durdurulmasını önler."
            )
        }
    }

    /**
     * Markaya özel otomatik başlatma / pil yönetimi ekranını güvenli açar.
     */
    fun openBrandSpecificSettings(context: Context): Boolean {
        val brand = getDeviceBrand()
        val intents = mutableListOf<Intent>()

        when (brand) {
            BrandType.XIAOMI -> {
                intents.add(Intent().setComponent(ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")))
            }
            BrandType.SAMSUNG -> {
                intents.add(Intent().setComponent(ComponentName("com.samsung.android.lool", "com.samsung.android.sm.battery.ui.BatteryActivity")))
                intents.add(Intent().setComponent(ComponentName("com.samsung.android.sm", "com.samsung.android.sm.ui.battery.BatteryActivity")))
            }
            BrandType.HUAWEI -> {
                intents.add(Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")))
                intents.add(Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.bootstart.BootStartActivity")))
            }
            BrandType.OPPO -> {
                intents.add(Intent().setComponent(ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")))
                intents.add(Intent().setComponent(ComponentName("com.oplus.battery", "com.oplus.battery.AppListActivity")))
            }
            BrandType.VIVO -> {
                intents.add(Intent().setComponent(ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")))
                intents.add(Intent().setComponent(ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.PurviewTabActivity")))
            }
            BrandType.STOCK_ANDROID -> {
                // Standart pil optimizasyonu
                intents.add(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
            }
        }

        for (intent in intents) {
            try {
                if (context !is android.app.Activity) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                return true
            } catch (_: Exception) {}
        }

        // Bulunamazsa standart uygulama detayına düş
        return openAppDetails(context)
    }

    fun openAppDetails(context: Context): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
                if (context !is android.app.Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }
}
