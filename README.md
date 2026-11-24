# JARVIS Android Rebuild 4.0
Clean Kotlin + Jetpack Compose Android assistant foundation for Android 8+.

Includes voice push-to-talk using Android speech UI, spoken responses, OpenRouter chat, app launch/search/dialer hand-off, local session memory/tasks/activity, dark cyan/emerald UI, optional Accessibility and Notification Listener setup.

Honest feature limits: no always-on wake word in this build; no SIM call audio capture or autonomous answering; SMS remains user-reviewed; no private DM scraping; no root/Shizuku/TV integration or bundled local model. Secure screens are not bypassed. User prompts are sent to OpenRouter only after key configuration and submission. API key field in this preview is session-only, not persisted.

Build: JDK 17, Gradle 8.9, Android SDK Platform 35; run gradle assembleDebug. GitHub Actions publishes a preview APK artifact.
