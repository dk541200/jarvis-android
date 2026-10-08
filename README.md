# JARVIS Local AI vNext

This source package is an incremental upgrade of the working Jarvis Android project.

## Included
- Persistent multi-session local chat history.
- History panel that opens/selects previous conversations.
- New Chat creates a separate session.
- Copy message and Copy Code actions.
- Lightweight Markdown-style headings/bullets and fenced-code rendering.
- Local memory for name and explicit remembered facts.
- Command router for deterministic phone actions (apps, Settings, timer, time/date, battery, volume, web search).
- Agent router for Command, Memory, Code, Study, Web and Local AI modes.
- JARVIS persona/context prompt with recent conversation + local memory.
- Background voice service with persistent notification, TTS, wake-word recognition and continuous re-listening without launching a new UI popup for every phrase.
- Custom notification layout with Open, Voice/Pause and Stop actions.
- Existing bundled Llama 3.2 1B Q4_0 local model workflow retained.

## Important Android limitation
The background service uses Android SpeechRecognizer. It can provide a continuous standby loop, but a truly system-level always-on hotword assistant that survives all Android restrictions should ultimately be migrated to Android VoiceInteractionService and selected as the device's default digital assistant. This package does not pretend that a generic foreground service is equivalent to that system role.

## Build
GitHub Actions downloads the official llama.cpp Android project and the bundled GGUF model, injects this app source, and builds an arm64 debug APK.

The package does not contain the large GGUF file itself.
