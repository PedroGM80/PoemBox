# --- Configuración Base ---
-keepattributes *Annotation*, Signature, EnclosingMethod, InnerClasses
-dontwarn javax.annotation.**

# --- Hilt / Dagger ---
-keep class dagger.hilt.android.internal.** { *; }
-keep class * extends androidx.lifecycle.ViewModel

# --- Room ---
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# --- MediaPipe LLM (IA On-Device) ---
# Muy importante para que no se rompa el motor de IA al ofuscar
-keep class com.google.mediapipe.tasks.genai.** { *; }
-keep class com.google.mediapipe.framework.image.** { *; }
-dontwarn com.google.mediapipe.**

# --- Firebase / Crashlytics ---
-keepattributes SourceFile,LineNumberTable
-keep public class com.google.firebase.** { *; }

# --- Billing (Compras In-App) ---
-keep class com.android.billingclient.api.** { *; }

# --- Modelos de Dominio (Serialización) ---
# Evitamos que se ofusquen los nombres de campos en la DB y JSON
-keepclassmembers class dev.pgm.poembox.domain.model.** { *; }
-keepclassmembers class dev.pgm.poembox.data.local.entities.** { *; }

# --- Jetpack Compose ---
-keep class androidx.compose.material3.** { *; }
-dontwarn androidx.compose.**
