package dev.pgm.poembox.presentation.ai

import android.app.ActivityManager
import android.content.Context
import android.os.Build

/**
 * Detecta qué nivel de IA on-device soporta el dispositivo.
 *
 *  LEVEL_RULES   – Siempre disponible. Sugerencias basadas en reglas fonéticas
 *                  del motor de sílabas ya existente. Sin ML, sin modelo.
 *
 *  LEVEL_LLMINFERENCE – Disponible en dispositivos con ≥4 GB RAM y Android 10+.
 *                  Puede ejecutar un modelo LLM cuantizado via MediaPipe
 *                  LLM Inference (el usuario debe descargar el modelo primero).
 *
 *  LEVEL_GEMINI_NANO – Disponible solo en dispositivos certificados con Android
 *                  AICore (Pixel 8+, Galaxy S24+, etc.). Sin descarga de modelo.
 *
 * En producción, siempre muestra al menos LEVEL_RULES.
 * Los niveles superiores se habilitan solo si el hardware lo permite,
 * y muestran una explicación clara al usuario si no están disponibles.
 */
enum class DeviceAILevel {
    LEVEL_RULES,
    LEVEL_LLM_INFERENCE,
    LEVEL_GEMINI_NANO
}

object DeviceAICapability {

    /** RAM mínima en bytes para LLM Inference (4 GB) */
    private const val MIN_RAM_FOR_LLM = 4L * 1024 * 1024 * 1024

    /**
     * Detecta el nivel máximo de IA disponible.
     * Siempre devuelve al menos [DeviceAILevel.LEVEL_RULES].
     */
    fun maxLevel(context: Context): DeviceAILevel {
        return when {
            isGeminiNanoAvailable(context) -> DeviceAILevel.LEVEL_GEMINI_NANO
            isLlmInferenceCapable(context) -> DeviceAILevel.LEVEL_LLM_INFERENCE
            else                           -> DeviceAILevel.LEVEL_RULES
        }
    }

    /** Devuelve true si el dispositivo tiene suficiente RAM y Android 10+ */
    fun isLlmInferenceCapable(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return false
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val info = ActivityManager.MemoryInfo()
        am.getMemoryInfo(info)
        return info.totalMem >= MIN_RAM_FOR_LLM
    }

    /**
     * Comprueba si Android AICore (Gemini Nano) está disponible.
     * Requiere Android 14 (API 34) y hardware compatible (Pixel 8+, Galaxy S24+...).
     * Se intenta via reflexión para no romper en dispositivos sin AICore.
     */
    fun isGeminiNanoAvailable(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) return false
        return try {
            // Intenta cargar la clase AICore en tiempo de ejecución.
            // Si no está presente, lanza ClassNotFoundException y devuelve false.
            Class.forName("android.app.ai.AIManager")
            true
        } catch (_: ClassNotFoundException) {
            false
        }
    }

    /** Nombre del nivel para mostrar al usuario */
    fun levelLabel(level: DeviceAILevel): String = when (level) {
        DeviceAILevel.LEVEL_RULES         -> "Asistente poético"
        DeviceAILevel.LEVEL_LLM_INFERENCE -> "IA local (LLM)"
        DeviceAILevel.LEVEL_GEMINI_NANO   -> "Gemini Nano ✨"
    }

    /** Descripción del porqué el nivel superior no está disponible */
    fun upgradeHint(currentLevel: DeviceAILevel): String? = when (currentLevel) {
        DeviceAILevel.LEVEL_RULES ->
            "Tu dispositivo no cumple los requisitos mínimos para IA local " +
            "(se necesitan ≥4 GB de RAM y Android 10+)."
        DeviceAILevel.LEVEL_LLM_INFERENCE ->
            "Gemini Nano requiere hardware certificado (Pixel 8+, Galaxy S24+) " +
            "con Android 14 o superior."
        DeviceAILevel.LEVEL_GEMINI_NANO -> null  // ya en el máximo
    }
}
