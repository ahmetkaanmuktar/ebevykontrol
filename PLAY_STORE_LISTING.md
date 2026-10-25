# Google Play Store Mağaza Listeleme & ASO Rehberi
**Uygulama:** Ebeveyn Denetimi / Parental Control

---

## 🇹🇷 TÜRKÇE MAĞAZA LİSTELEME

### 1. Uygulama Adı (Maksimum 30 Karakter)
`Ebeveyn Denetimi: Ekran Süresi`

### 2. Kısa Açıklama (Maksimum 80 Karakter)
`Çocuklar için güvenli ekran süresi sınırlayıcı, uygulama kilidi ve ebeveyn kalkanı.`

### 3. Uzun Açıklama (Maksimum 4.000 Karakter)
```
🌱 ÇOCUKLARINIZ İÇİN SAĞLIKLI DİJİTAL ALIŞKANLIKLAR OLUŞTURUN!

Ebeveyn Denetimi, çocuklarınızın akıllı telefon ve tabletlerde geçirdiği süreyi yönetmenizi, oyun ve sosyal medya bağımlılığını önlemenizi sağlayan yeni nesil, modern ve güvenilir bir ebeveyn asistanıdır.

Kişisel verileri gözetleyen veya karmaşık hesap açma süreçleri gerektiren diğer uygulamaların aksine, Ebeveyn Denetimi %100 yerel, internetsiz ve gizlilik odaklı çalışır.

✨ ÖNE ÇIKAN ÖZELLİKLER:

⏱️ Uygulama Bazlı Günlük Süre Sınırı
YouTube, TikTok, Instagram veya popüler oyunlar için ayrı ayrı günlük kullanım süreleri belirleyin (Örn: YouTube 45 dk, Oyunlar 30 dk). Belirlenen süre dolduğunda uygulama otomatik olarak kilitlenir.

🔒 Kırılamaz Ebeveyn PIN Koruması & Biyometrik Onay
Gelişmiş PBKDF2 kriptografik şifreleme ve biyometrik parmak izi / yüz tanıma koruması sayesinde çocuğunuz süreyi kendi başına artıramaz veya korumayı devre dışı bırakamaz.

🌱 Nazik "Dijital Mola" Kilit Ekranı
Süre dolduğunda çocuğu azarlamayan veya korkutmayan, gerçek dünyayı keşfetmeye davet eden nefes alan modern ve göz yormayan kilit kalkanı devreye girer.

⚡ Hızlı +15 Dakika Ek Süre
Çocuğunuz ders çalışırken veya özel bir durumda ek süreye ihtiyaç duyduğunda, ebeveyn tek dokunuşla parmak iziyle 15 dakika ek süre tanıyabilir.

🛡️ %100 Gizli, Güvenli ve Çevrimdışı (İnternetsiz)
• İnternet izni istemez (Sıfır veri sızıntısı).
• Mesajları, fotoğrafları, şifreleri veya kişisel yazışmaları KESİNLİKLE görmez ve okumaz.
• Tüm veriler yalnızca sizin cihazınızda güvenle saklanır.

🔋 Pil Dostu ve Hafif Tasarım
Sürekli çalışan ağır servisler yerine yalnızca pencere değişimlerinde akıllıca çalışan ultra hafif mimari ile pil tüketmez.

Samsung, Xiaomi, Redmi, POCO, Huawei, OPPO, Vivo ve saf Android cihazlarla tam uyumludur.

Hemen indirin ve çocuğunuzun dijital dünyada sağlıklı sınırlar içinde güvende kalmasını sağlayın!
```

---

## 🇺🇸 İNGİLİZCE MAĞAZA LİSTELEME (GLOBAL)

### 1. App Title (Max 30 Chars)
`Parental Control: Screen Time`

### 2. Short Description (Max 80 Chars)
`Limit screen time, block addictive apps & build healthy digital habits safely.`

### 3. Full Description (Max 4,000 Chars)
```
🌱 BUILD HEALTHY DIGITAL HABITS FOR YOUR CHILDREN!

Parental Control is a modern, lightweight, and privacy-first digital companion designed to help parents manage screen time and curb app addiction without invading children's privacy.

Unlike complex parental trackers that require multiple account setups or upload sensitive data to cloud servers, Parental Control works 100% offline on your device.

✨ KEY FEATURES:

⏱️ Daily App Screen Time Limits
Set daily usage limits for social media, streaming apps, or games (e.g., YouTube: 45 mins, Roblox: 30 mins). Once the quota is reached, the app is automatically locked.

🔒 Unbreakable PIN & Biometric Protection
Protected by enterprise-grade PBKDF2WithHmacSHA256 encryption with brute-force lockout defense. Parents can authorize settings or temporary time extensions via fingerprint / Face Unlock in seconds.

🌱 Gentle "Digital Break" Lock Screen
When time runs out, a beautifully designed, OLED-friendly calm lock screen reminds your child that it’s time to take a break and step into the real world.

⚡ Instant +15 Min Extension
Need quick extra time for homework or study? Parents can grant a 15-minute time extension with a single fingerprint tap.

🛡️ 100% Private, Safe & Offline (No Internet Needed)
• Does NOT require the INTERNET permission.
• NEVER inspects private messages, photos, keystrokes, or passwords.
• Zero remote tracking, zero advertisements, zero analytics tracking.

🔋 Ultra Battery-Friendly
Engineered with lightweight event-driven architecture that preserves your phone's battery life throughout the day.

Compatible with Samsung, Xiaomi, Redmi, POCO, Huawei, OPPO, Vivo, and stock Android devices.

Download now to protect your family's digital well-being!
```

---

## 📋 GOOGLE PLAY CONSOLE FORMLARI İÇİN HAZIR BEYANLAR

### 1. Erişilebilirlik Hizmeti Beyanı (Accessibility Tool Declaration)
Play Console'da "Erişilebilirlik API'si beyanı" sorulduğunda girilecek metin:
> *"Parental Control uses the Android AccessibilityService API strictly to monitor foreground app package names to enforce parental daily screen time limits. When a child reaches the daily limit configured by the parent, the app displays a lock screen overlay. The service has 'canRetrieveWindowContent' set to false and strictly does NOT monitor, collect, or transmit any user inputs, keystrokes, messages, screen contents, or personal information. The app does not request the INTERNET permission and operates entirely offline on-device."*

### 2. Veri Güvenliği (Data Safety) Formu
- **Uygulama veri topluyor mu veya paylaşıyor mu?** -> **HAYIR** (No)
- *Gerekçe:* Uygulamada `INTERNET` izni yoktur. Hiçbir veri cihaz dışına çıkmaz.

### 3. Hedef Kitle (Target Audience)
- **Hedef yaş grubu:** 18 yaş ve üzeri (Ebeveynler için ebeveyn denetimi aracı).  
*(Önemli: "Çocuklar" seçilirse Google'ın Designed for Families politikasına girer ve gereksiz bürokrasi artar. Uygulama ebeveyn aracı olarak beyan edilmelidir.)*
