# Proguard / R8 rules for Ebeveyn Denetimi (Parental Control App)

# 1. Room Veritabanı ve Entity / DAO Koruma
-keep class com.ahmetkaan.uygulamakapatma.data.local.entity.** { *; }
-keep class com.ahmetkaan.uygulamakapatma.data.local.dao.** { *; }
-keep class com.ahmetkaan.uygulamakapatma.data.local.AppDatabase { *; }
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# 2. Coroutines & Flow
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# 3. Kriptografi ve Güvenlik (PBKDF2)
-keep class javax.crypto.** { *; }
-keep class java.security.** { *; }

# 4. Servisler ve Alıcılar
-keep class com.ahmetkaan.uygulamakapatma.service.AppBlockerAccessibilityService { *; }
-keep class com.ahmetkaan.uygulamakapatma.receiver.BootReceiver { *; }
-keep class com.ahmetkaan.uygulamakapatma.ui.block.BlockOverlayActivity { *; }

# 5. Jetpack Compose
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
