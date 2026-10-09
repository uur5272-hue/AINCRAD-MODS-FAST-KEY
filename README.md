# Aincrad Key Assistant

A sleek, modern Android application built with **Kotlin** and **Jetpack Compose**, featuring a cyber dark theme with neon-cyan and electric-blue accents. Designed for a reliable, guided, one-tap verification and access key retrieval workflow.

---

## Key Features

1. **One-Tap Workflow:**
   - **GET KEY:** Prominently placed on the home screen to instantly start the verification process.
   - **Continuous Progression:** Seamlessly guides the user through required website instructions to the final key generation page.

2. **Configured Website URLs:**
   - **Step 1 (Initial / Instructions):**  
     `https://rodaemotor.com/toyota-lanca-gr-kart-para-facilitar-a-entrada-de-familias-no-automobilismo/`
   - **Step 2 (Key Generation Portal):**  
     `https://aincradmods.com/getkey`

3. **Secure In-App Browser Experience:**
   - **Navigation Controls:** Back, Forward, Reload/Retry, and Open in External Browser.
   - **Step 1 Action Dock:** Sticky banner with the **`CONTINUE TO KEY PAGE`** button.
   - **Step 2 Action Dock:** Sticky banner with the **`COPY KEY`** button and automated key inspector.
   - **Strict HTTPS Validation:** Automatically verifies secure SSL connections and blocks unencrypted protocols.
   - **Error Resilience:** Real-time loading indicator and network error cards with instant retry.

4. **Key Vault & Local History:**
   - Automatically stores retrieved keys in private local app storage.
   - Displays timestamps and provides instant one-tap copy and deletion.

5. **Security & Anti-Bot Compliance:**
   - Does **not** bypass CAPTCHAs, Cloudflare checks, or sponsor requirements—ensures legitimate completion of website rules.
   - Never intercepts, logs, or stores passwords, cookies, or private tokens.

---

## Project Structure

```
├── .github/
│   └── workflows/
│       └── build-apk.yml          # GitHub Actions workflow to build & upload debug APK
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/
│   │   │   │   ├── MainActivity.kt               # Entry point and screen transitions
│   │   │   │   ├── data/
│   │   │   │   │   ├── WorkflowConstants.kt       # Target URLs and security validation
│   │   │   │   │   ├── SavedKey.kt                # Data model for stored keys
│   │   │   │   │   └── KeyVaultRepository.kt      # SharedPreferences persistence
│   │   │   │   ├── ui/
│   │   │   │   │   ├── MainViewModel.kt           # State management & workflow logic
│   │   │   │   │   ├── home/HomeScreen.kt         # Modern dashboard with GET KEY button
│   │   │   │   │   ├── browser/SecureBrowserScreen.kt # In-app WebView with step controls
│   │   │   │   │   ├── components/NeonComponents.kt # Neon buttons, cards & badges
│   │   │   │   │   └── theme/                     # Dark neon theme, colors & typography
│   │   │   │   └── res/                           # Drawables, mipmaps, strings
│   │   │   └── AndroidManifest.xml                # Permissions (INTERNET, ACCESS_NETWORK_STATE)
│   │   └── test/                                  # Unit and Robolectric tests
│   └── build.gradle.kts                           # App build configuration
├── gradle/
│   └── libs.versions.toml                         # Dependency catalog
├── metadata.json                                  # AI Studio platform metadata
└── settings.gradle.kts                            # Gradle settings
```

---

## How to Build the APK Locally

### Requirements
- **Java Development Kit (JDK):** Version 17 or 21
- **Android SDK:** Compile SDK 36 (Minimum SDK 24 - Android 7.0+)
- **Gradle:** 8.11+

### Build Commands

```bash
# 1. Clone the repository
git clone <YOUR_REPO_URL>
cd aincrad-key-assistant

# 2. Build the Debug APK
gradle :app:assembleDebug

# 3. Locate the generated APK
# Path: app/build/outputs/apk/debug/app-debug.apk
```

---

## Automatic Build via GitHub Actions

This project includes a ready-to-use GitHub Actions workflow located at `.github/workflows/build-apk.yml`.

### How to Download the APK from GitHub Actions:
1. Push your code to your GitHub repository or navigate to the **Actions** tab on GitHub.
2. Select the **Build Debug APK** workflow.
3. Click **Run workflow** (or trigger it automatically with a push to `main`).
4. Once the build finishes (typically ~1-2 minutes), scroll down to the **Artifacts** section at the bottom of the run page.
5. Click **Aincrad-Key-Assistant-debug-apk** to download the ZIP file containing `app-debug.apk`.

---

## How to Install on Android

1. Transfer or download `app-debug.apk` directly to your Android device.
2. Tap the downloaded file in your **Downloads** or **Files** app.
3. If prompted with *"For your security, your phone is not allowed to install unknown apps from this source"*:
   - Tap **Settings**.
   - Enable **Allow from this source**.
4. Tap **Install**.
5. Open **Aincrad Key Assistant** from your app drawer.
