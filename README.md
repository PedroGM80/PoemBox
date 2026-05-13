# PoemBox

**PoemBox** es una aplicación Android avanzada diseñada para poetas y escritores. No solo permite la creación y almacenamiento de poemas, sino que también ofrece herramientas de análisis métrico y estructural en tiempo real.

![Logo](./poemform.png)

## Características

- **Análisis Métrico:** Conteo automático de sílabas, identificación de rimas y clasificación de versos.
- **Análisis Estructural:** Detección de encabalgamientos y esticomitia.
- **Gestión de Borradores:** Guarda tus ideas y poemas en progreso de forma local y segura.
- **Autenticación de Usuarios:** Sistema de gestión de sesiones para mantener tu contenido organizado.
- **Interfaz Moderna:** Desarrollada íntegramente con **Jetpack Compose** siguiendo las guías de Material Design 3.
- **Navegación Intuitiva:** Sistema de pestañas para alternar entre edición, gestión y monitoreo.

## Stack Tecnológico

- **Lenguaje:** [Kotlin](https://kotlinlang.org/)
- **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
- **Arquitectura:** Clean Architecture + MVVM
- **Inyección de Dependencias:** [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- **Base de Datos:** [Room](https://developer.android.com/training/data-storage/room)
- **Navegación:** [Compose Navigation](https://developer.android.com/jetpack/compose/navigation)
- **Procesamiento:** KSP (Kotlin Symbol Processing)
- **Asincronía:** Corrutinas y Flow
- **Gestión de Datos:** DataStore para sesiones de usuario.

## Estructura del Proyecto

El proyecto sigue los principios de **Clean Architecture**, dividido en tres módulos principales:

- **`:app`:** Contiene la capa de presentación (UI, ViewModels, Composables) y la configuración de Hilt.
- **`:domain`:** Contiene la lógica de negocio pura: modelos de dominio, interfaces de repositorios y casos de uso. No tiene dependencias de Android.
- **`:data`:** Implementación de los repositorios, acceso a base de datos Room, mappers y fuentes de datos locales.

## Instalación y Uso

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/PedroGM80/PoemBox.git
   ```
2. **Abrir en Android Studio:**
   Asegúrate de tener la versión más reciente de Android Studio (Ladybug o superior preferiblemente).
3. **Sincronizar Gradle:**
   El proyecto utiliza Version Catalogs (`libs.versions.toml`) para la gestión de dependencias.
4. **Ejecutar:**
   Conecta un dispositivo físico o inicia un emulador con API 24 o superior.

## Licencia

Este proyecto es de uso personal/educativo. Consulta al autor para más detalles sobre su distribución.

---

## Autor

**Pedro Gallego Morales**
- GitHub: [@PedroGM80](https://github.com/PedroGM80)
- [Descargar APK](https://github.com/PedroGM80/PoemBox/releases/download/PoemBox/PoemBox.apk)
