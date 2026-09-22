# 🎙️ Speech-Based Examination — Android App

An accessible Android examination app that lets students with **visual or physical disabilities** take exams independently. Questions are **read aloud** using text-to-speech, answers are **captured by voice** using speech-to-text, and results are evaluated and displayed — all hands-free.

> Based on the approach described in the associated IEEE publication on speech-based examination systems for accessibility.

---

## ✨ Features
- 🔊 **Text-to-speech** — reads each question aloud to the student.
- 🎤 **Speech-to-text** — captures the student's spoken answers.
- ✅ **Automatic evaluation** — compares answers and computes results.
- 👤 **User accounts** — registration, login, profile, and password management via Firebase Authentication.
- 📊 **Results screen** — shows marks after the exam.

## 🧱 Tech stack
| Area | Technology |
|---|---|
| Language | Java |
| Platform | Android (min SDK 21, target/compile SDK 34) |
| Auth & data | Firebase Authentication, Realtime Database, Storage |
| Speech | Android `TextToSpeech` + Google speech recognition |
| UI | AndroidX (AppCompat, Material Components, ConstraintLayout) |
| Images | Picasso |
| Build | Gradle 8.2, Android Gradle Plugin 8.2.2, JDK 17 |

---

## 🚀 Getting started

### Prerequisites
- **Android Studio** (Hedgehog / 2023.1 or newer recommended)
- **JDK 17**
- An Android device or emulator (API 21+)

### Setup
1. **Clone the repo**
   ```bash
   git clone https://github.com/<your-username>/Speech-Based-Examination-Android-App.git
   ```
2. **Firebase configuration** — the app needs a `google-services.json` in the `app/` folder.
   - A sample config is included, but for your own build you should create a Firebase project at [console.firebase.google.com](https://console.firebase.google.com), enable **Authentication**, **Realtime Database**, and **Storage**, and download your own `app/google-services.json`.
3. **Open in Android Studio** → let Gradle sync → **Run ▶** on a device/emulator.
4. Grant the **microphone** permission when prompted (required for speech recognition).

---

## ♿ Purpose & impact
This project was built to make examinations **accessible** to students who cannot read a screen or use their hands to type. By combining audio question delivery with voice-based answering, it removes barriers to independent assessment.

---

## 🔧 Modernization notes
This codebase was originally written in **2019** and has been modernized to build on current tooling:
- Migrated from the deprecated **Android Support Libraries → AndroidX**.
- Replaced the shut-down **`jcenter()`** repository with **`mavenCentral()`**.
- Upgraded **Gradle 5.6 → 8.2** and **AGP 3.6 → 8.2.2** (JDK 17).
- Raised **compile/target SDK 27 → 34**; introduced the `namespace` DSL.
- Consolidated **Firebase** dependencies under the Firebase **BoM**.
- Cleaned repository hygiene (removed committed IDE files, build artifacts, and stray metadata; added a proper `.gitignore`).

> ⚠️ The modernization updates the build configuration and source imports to current standards. Verify the build in Android Studio and address any remaining API-level adjustments before release.

---

## 📄 License
Add a license of your choice (e.g., MIT) if you intend others to reuse this code.
