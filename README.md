Web-2-apk

Web-2-apk is an Android application for turning web-based applications into installable APKs through a guided configuration and build workflow.

The app allows users to provide a website URL, configure app behavior and permissions, customize branding and UI, and generate an Android application package from the configured web app.

---

✨ Features

- 🚀 Guided Web-to-APK setup workflow
- 🌐 Website URL and scope configuration
- 🔐 Permission and app behavior configuration
- 🎨 App identity and branding customization
- 🖼️ Custom app assets and UI configuration
- ⚙️ Automatic Android manifest and project generation
- 🔨 APK build and packaging engine
- 📊 Build progress and status monitoring
- 📜 Build history tracking
- 📱 APK installation utilities
- 🧪 Built-in simulator and preview screens
- ⚙️ Application settings
- 🔥 Firebase integrations
- 🌍 Networking and API integrations

---

🛠️ Tech Stack

- Kotlin
- Jetpack Compose
- Gradle Kotlin DSL
- Room
- Retrofit
- OkHttp
- Moshi
- Firebase AI
- Firebase App Check
- Coil
- Kotlin Coroutines

---

📁 Repository Structure

Web-2-apk/
├── .env.example
├── .gitignore
├── build.gradle.kts
├── gradle.properties
├── gradle/
├── metadata.json
├── settings.gradle.kts
│
└── app/
    ├── build.gradle.kts
    ├── proguard-rules.pro
    │
    └── src/
        ├── androidTest/
        │
        ├── main/
        │   ├── AndroidManifest.xml
        │   │
        │   ├── java/com/example/
        │   │   ├── MainActivity.kt
        │   │   ├── Web2ApkApp.kt
        │   │   │
        │   │   ├── builder/
        │   │   ├── data/
        │   │   ├── downloader/
        │   │   ├── ui/
        │   │   └── utils/
        │   │
        │   └── res/
        │
        └── test/

Key Source Areas

Directory| Description
"builder/"| APK generation, manifest generation and build logic
"data/"| Data models, database and repositories
"ui/"| Jetpack Compose screens and UI components
"downloader/"| Web, font and media handling utilities
"utils/"| Installation, networking and helper utilities

---

🚀 Getting Started

Prerequisites

Before building the project, make sure you have:

- Android Studio
- JDK 11 or newer
- Android SDK configured
- Gradle support through Android Studio
- A configured Firebase project if Firebase features are enabled

---

📥 Clone the Repository

git clone https://github.com/Kryvex1/Web-2-apk.git
cd Web-2-apk

---

🔑 Environment Configuration

The project includes an ".env.example" file containing the required environment configuration.

Create your local environment file:

cp .env.example .env

Configure the required values before building if your setup requires them.

Release Signing

Release builds can use the following environment variables:

KEYSTORE_PATH
STORE_PASSWORD
KEY_PASSWORD

If these variables are not configured, the build system may fall back to the default keystore configuration defined by the project.

«Security: Never commit real passwords, API keys, Firebase secrets, or private keystore files to GitHub.»

---

🔨 Building the App

Open the project in Android Studio and allow Gradle to synchronize.

You can then either:

Debug Build

Run the application directly from Android Studio on an emulator or Android device.

Release Build

Use the appropriate Gradle release task to generate a signed release APK.

The project contains both:

- Debug build variant
- Release build variant

Android Configuration

Configuration| Value
Minimum SDK| 26
Target SDK| 35
Language| Kotlin
UI Framework| Jetpack Compose

---

🔐 Signing

The release build supports custom APK signing through environment variables.

Keep your signing credentials private and never upload your keystore or passwords to the repository.

If you publish your own release APK, make sure it is signed with a secure private release key.

---

🔥 Firebase

Some features of Web-2-apk use Firebase services.

If you intend to use those features, configure your own Firebase project and provide the required configuration files and credentials according to the project's setup.

Do not commit private Firebase credentials or other sensitive configuration values.

---

📱 What Web-2-apk Does

Web-2-apk is designed around a simple workflow:

Website URL
     ↓
App Configuration
     ↓
Permissions & Behavior
     ↓
Branding & Identity
     ↓
Project Generation
     ↓
APK Build
     ↓
Install / Export

The goal is to make the process of creating an Android wrapper/application for a web-based project easier through a single guided interface.

---

📌 Project Status

Web-2-apk is an actively developed project.

Features, UI components, build functionality and integrations may change as development continues.

---

👨‍💻 Author

Umang Sattawan

GitHub:
https://github.com/Kryvex1

---

📄 License

Copyright © 2026 Umang Sattawan. All Rights Reserved.

This project and its source code are proprietary to Umang Sattawan.

The source code is publicly available for viewing and reference purposes only. No permission is granted to copy, modify, distribute, sublicense, publish, sell, or use this project or substantial portions of its source code for commercial or personal projects without prior written permission from the copyright holder.

For permission to use, modify, distribute, or commercially use this project, please contact the copyright holder.

See the ""LICENSE"" (LICENSE) file for the complete license terms.

---

⚠️ Disclaimer

Web-2-apk is provided for legitimate development and application-packaging purposes.

Users are responsible for ensuring that the websites, content, assets, APIs, trademarks, and other resources they package or access through the application are used in accordance with the applicable laws and the respective owners' terms and permissions.

---

⭐ About

Web-2-apk is a Kotlin-based Android application built with Jetpack Compose that provides a guided workflow for converting web applications into packaged Android applications.

It combines configuration, customization, project generation, build automation and APK management into a single Android application.
