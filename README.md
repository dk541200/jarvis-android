# JARVIS Android Rebuild 4.0

A Kotlin + Jetpack Compose Android assistant foundation for Android 8+.

## Implemented in this preview
- Dark cyan/emerald dashboard with Home, Agents, Activity and Settings.
- Push-to-talk using Android's speech-recognition UI; the recognized phrase is placed in chat for review before sending.
- Microphone permission is checked before speech recognition launches.
- Spoken responses using Android TextToSpeech.
- OpenRouter chat using the user's own key, with recent conversation context (key remains session-only).
- Basic app launch, browser-search hand-off and dialer hand-off.
- Local persistent storage for memories, tasks and the latest 100 conversation entries.
- A task can schedule a local notification reminder for 10 minutes later.
- Optional Accessibility and Notification Listener settings shortcuts.

## Not implemented yet
- Always-on wake word or reliable background microphone service.
- Full screen automation/action bridge; the accessibility service is only a permission/setup foundation.
- Notification reading, storage or summarization.
- Recurring reminders, reminder editing/snoozing, and background agent execution.
- SIM-call audio capture, automatic answering or call recording.
- WhatsApp/Instagram private-message automation.
- Shizuku, root, smart-TV integration, specialist autonomous agents or bundled local LLM.

Secure screens are not bypassed. Calls and messages remain user-controlled. Cloud prompts are sent to OpenRouter only after the user configures a key and submits a prompt. The API key is held only in the current app session; it is not saved to disk. Reminder notifications require Android notification permission to be enabled.

## Build
Use JDK 17, Gradle 8.9 and Android SDK Platform 35. Run `gradle assembleDebug`.
GitHub Actions builds the preview APK for the `jarvis-rebuild` branch.
