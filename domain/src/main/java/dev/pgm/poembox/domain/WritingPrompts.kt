package dev.pgm.poembox.domain

import java.util.Calendar

object WritingPrompts {

    private val prompts = listOf(
        "El último tren de midnight",
        "Lluvia sobre el tejado de mi infancia",
        "Una carta que nunca envié",
        "El silencio entre dos palabras",
        "Lo que el mar se lleva al amanecer",
        "Retrato de una ciudad dormida",
        "El jardín que ya no existe",
        "Manos que construyeron el mundo",
        "La luz que entra por la ventana rota",
        "Memoria de un verano sin nombre",
        "Lo que queda cuando todo se va",
        "El olor a tierra mojada",
        "Voces en el umbral del sueño",
        "Una estrella que nadie ha visto nacer",
        "El tiempo detenido en una fotografía",
        "Conversación con mi sombra",
        "El peso de las palabras no dichas",
        "Pájaros que cruzan el invierno",
        "La casa donde nació el viento",
        "Pequeñas muertes cotidianas",
        "Un espejo que recuerda otro rostro",
        "El último día del verano",
        "Raíces que buscan el cielo",
        "La música que aprendí de tu ausencia",
        "Ciudades imaginadas al atardecer",
        "Lo que el fuego sabe del frío",
        "El color del olvido",
        "Noche de luna sobre el río",
        "Infancia: ese país extranjero",
        "La distancia que nos hace cercanos",
        "Un poema sin título ni autor",
        "El nombre secreto de las cosas",
        "Cuando el tiempo vuelve sobre sí mismo",
        "La orilla donde termina el mapa",
        "Palabras que aprendí demasiado tarde"
    )

    fun todayPrompt(): String {
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        return prompts[dayOfYear % prompts.size]
    }
}
