# JARVIS Android Rebuild 4.0

A Kotlin + Jetpack Compose Android assistant foundation for Android 8+.

## Implemented in this branch
- Dark cyan/emerald dashboard with Home, Agents, Activity and Settings.
- Push-to-talk using Android's speech-recognition UI; microphone permission is requested before recognition starts.
- Spoken responses using Android TextToSpeech.
- OpenRouter chat using the user's own key (session-only in this preview).
- Basic app launch, web-search hand-off and dialer hand-off.
- Local persistent storage for memories, tasks and the latest 100 conversation entries using private app preferences.
- Optional Accessibility and Notification Listener setup screens.

## Not implemented yet
- Always-on wake word or reliable background microphone service.
- Full screen automation/action bridge; the accessibility service is only a permission/setup foundation.
- Notification reading, storage or summarization.
- Scheduled reminders and background task execution.
- SIM-call audio capture, automatic answering or call recording.
- WhatsApp/Instagram private-message automation.
- Shizuku, root, smart-TV integration, specialist autonomous agents or bundled local LLM.

Secure screens are not bypassed. Calls and messages remain user-controlled. Cloud prompts are sent to OpenRouter only after the user configures a key and submits a prompt. The API key is held only in the current app session; it is not saved to disk.

## Build
Use JDK 17, Gradle 8.9 and Android SDK Platform 35. Run `gradle assembleDebug`.
GitHub Actions builds the preview APK for the `jarvis-rebuild` branch.
