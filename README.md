# PoemBox ✍️

[![CI](https://github.com/PedroGM80/PoemBox/actions/workflows/ci.yml/badge.svg)](https://github.com/PedroGM80/PoemBox/actions/workflows/ci.yml)
[![License: Personal](https://img.shields.io/badge/license-personal-blue.svg)](#licencia)

**PoemBox** es una app Android para poetas que quieren escribir, analizar y compartir su obra. Combina un motor métrico propio con IA on-device para ofrecer sugerencias de rima y continuación de versos sin depender de servicios en la nube.

---

## ✨ Características

| Módulo | Qué hace |
|--------|----------|
| **Editor** | Contador de sílabas en tiempo real, validación de formas (haiku, soneto, décima…), notas del poema |
| **Análisis** | Métrica predominante, estructura de estrofas, tipo de rima, encabalgamiento |
| **Biblioteca** | 7 formas poéticas con ejemplos de Lorca, Neruda, Machado, Sor Juana… cargables en el editor |
| **Asistente IA** | Sugerencias de rima (siempre) + continuación de verso via LLM on-device (≥4 GB RAM) o Gemini Nano (Pixel 8+) |
| **Compartir** | Tarjeta visual con imagen de fondo, PDF con análisis, texto plano |
| **Widget** | Muestra el último poema y la racha de escritura en la pantalla de inicio |
| **Racha diaria** | Registra los días consecutivos escribiendo; visible en el perfil |
| **Onboarding** | 3 slides la primera vez; evita mostrarlos de nuevo |
| **Premium** | Botón "Apoya PoemBox ⭐" via Play Billing (pago único) |

---

## 🏗️ Arquitectura

```
PoemBox/
├── app/        # UI (Compose + Material 3), ViewModels, DI, Workers, Widgets
├── domain/     # Lógica pura: motor de sílabas, casos de uso, interfaces
└── data/       # Room, DataStore, repositorios
```

**Stack:** Kotlin · Jetpack Compose · Material 3 · Hilt · Room · DataStore · WorkManager · Firebase (Analytics + Crashlytics) · MediaPipe LLM · Jetpack Glance · Play Billing

---

## 🚀 Primeros pasos

```bash
git clone https://github.com/PedroGM80/PoemBox.git
```

Abre el proyecto en **Android Studio Meerkat** o superior y pulsa Run.

Para release local crea `keystore.properties` (ver `keystore.properties.template`).

---

## 🔄 CI/CD

El pipeline de GitHub Actions (`.github/workflows/ci.yml`) ejecuta:

1. **Lint** — `lintDebug` + análisis Codacy
2. **Tests** — `:domain:test` + `:app:testDebugUnitTest` (214 tests)
3. **Build Release** — `bundleRelease` + `assembleRelease` firmado con el keystore guardado como secret
4. **Tag automático** — lee `version.properties` y crea el tag `v{versionName}` en master
5. **GitHub Release** — sube el AAB y el APK al release cuando se push un tag `v*`

Para publicar una nueva versión:

```
GitHub Actions → Trigger New Release → elegir patch / minor / major
```

### Secrets necesarios en GitHub

| Secret | Descripción |
|--------|-------------|
| `RELEASE_KEYSTORE` | Keystore en Base64: `base64 -i poembox.jks` |
| `RELEASE_KEYSTORE_PASSWORD` | Contraseña del keystore |
| `RELEASE_KEY_ALIAS` | Alias de la clave |
| `RELEASE_KEY_PASSWORD` | Contraseña de la clave |
| `GOOGLE_SERVICES_JSON` | Contenido de `google-services.json` de Firebase |
| `CODACY_PROJECT_TOKEN` | Token del proyecto en Codacy (opcional) |

---

## 🧪 Tests

```bash
./gradlew :domain:test :app:testDebugUnitTest
```

214 tests — 0 fallos. Cobertura por módulo:

| Módulo | Tests | Qué cubre |
|--------|-------|-----------|
| `:domain` | 109 | UtilitySyllables, PoemUtils, PoemMetric, StreakCalculator |
| `:app` | 105 | RhymeSuggester, DeviceAICapability, FormsLibrary, ViewModels (Edit, Monitoring, Auth, Stats, PoetryAssistant) |

---

## 📄 Licencia

**Autor:** Pedro Gallego Morales ([@PedroGM80](https://github.com/PedroGM80))

Distribuido para uso personal y educativo. Para uso comercial o derivados, contacta al autor.

---

*Desarrollado con alma por TeckelSoft 🐾*
