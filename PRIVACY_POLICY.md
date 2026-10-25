# Privacy Policy — Parental Control (Ebeveyn Denetimi)
**Last Updated:** October 25, 2026

Welcome to **Parental Control** ("we", "our", or "the app"). We respect your privacy and are committed to protecting it. This Privacy Policy explains our practices regarding your data.

---

### 1. Zero Data Collection & 100% Offline Architecture
Our app is built on a **local-first, offline-by-design** principle:
- **No Internet Permission:** The application does NOT request or use the Android `android.permission.INTERNET` permission.
- **No Remote Servers:** We do not operate any external database, cloud server, or remote storage.
- **Zero Third-Party Sharing:** Your data never leaves your device. We do not sell, rent, monetize, or transmit any user data to third parties, advertising networks, or analytics providers.

---

### 2. Permissions & How We Use Them

#### A) Android AccessibilityService API (`BIND_ACCESSIBILITY_SERVICE`)
- **Purpose:** To detect when your child launches a daily time-restricted application and display the parental break screen (`BlockOverlayActivity`) when time limits are reached.
- **What is Monitored:** Only the application package name of the active foreground window.
- **What is NEVER Monitored or Collected:** 
  - Keystrokes, passwords, private chats, emails, or personal text inputs are **STRICTLY NOT MONITORED OR RECORDED**.
  - Screen contents are never captured (`canRetrieveWindowContent="false"`).
  - Audio, camera, photos, or browsing history are NEVER accessed.
- **Prominent Disclosure:** The app explicitly informs you and requests your consent before directing you to enable the Accessibility Service in Android Settings.

#### B) Package Usage Stats (`PACKAGE_USAGE_STATS`)
- **Purpose:** To calculate daily screen time usage (in minutes) for installed applications, enabling parents to enforce daily screen time limits.
- **Storage:** All calculations are performed on-device and stored in an encrypted local database.

#### C) Display Over Other Apps (`SYSTEM_ALERT_WINDOW`)
- **Purpose:** To present the "Digital Break" lock overlay when an app's configured time limit has expired.

#### D) Biometric Authentication (`USE_BIOMETRIC`)
- **Purpose:** To allow parents to securely authenticate with fingerprint or facial recognition to adjust limits or grant temporary time extensions (+15 mins). Biometric data is processed exclusively by the Android secure hardware keystore and is never accessed by the app.

---

### 3. Data Storage & Security
- **PIN Security:** Your parental PIN is never saved in plain text. It is cryptographically hashed using **PBKDF2WithHmacSHA256** with 10,000 iterations and a unique 16-byte cryptographic salt.
- **Brute-Force Protection:** Progressive lockout timers prevent unauthorized guessing attempts.
- **Backup Exclusion:** The app explicitly excludes databases and local preferences from cloud backup (`android:allowBackup="false"` and `data-extraction-rules.xml`).

---

### 4. Children’s Privacy (COPPA & GDPR-K Compliance)
This application is designed as a tool for parents and legal guardians. Because our application does not collect, transmit, or process any personal identification data, it fully complies with the Children's Online Privacy Protection Act (COPPA) and General Data Protection Regulation (GDPR).

---

### 5. Changes to This Policy
Any updates to this Privacy Policy will be reflected with a revised date. As the app does not connect to the Internet, policy changes will only be distributed through official Google Play Store application updates.

---

### 6. Contact Us
If you have questions or suggestions regarding this Privacy Policy, please contact the developer via the official Google Play Store developer contact email.
