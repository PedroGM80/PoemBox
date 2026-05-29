package dev.pgm.poembox.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.pgm.poembox.R
import dev.pgm.poembox.presentation.theme.Dimens
import dev.pgm.poembox.presentation.theme.PoeticFont

// ─── Modelo de datos ───────────────────────────────────────────────────────

data class FormExample(
    val id: String,
    val name: String,
    val emoji: String,
    val structure: String,       // descripción de la estructura
    val rhymeScheme: String,     // esquema de rima
    val author: String,
    val title: String,
    val poem: String             // texto completo del poema ejemplo
)

val FORM_EXAMPLES = listOf(
    FormExample(
        id = "haiku",
        name = "Haiku",
        emoji = "🌸",
        structure = "3 versos: 5 – 7 – 5 sílabas",
        rhymeScheme = "Sin rima obligatoria",
        author = "Matsuo Bashō",
        title = "El viejo estanque",
        poem = "Un viejo estanque.\nSalta una rana. El sonido\ndel agua."
    ),
    FormExample(
        id = "soneto",
        name = "Soneto",
        emoji = "🕊️",
        structure = "14 versos endecasílabos (11 sílabas): 2 cuartetos + 2 tercetos",
        rhymeScheme = "ABBA ABBA CDC DCD",
        author = "Lope de Vega",
        title = "Soneto del amor",
        poem = "Desmayarse, atreverse, estar furioso,\náspero, tierno, liberal, esquivo,\nalentado, mortal, difunto, vivo,\nleal, traidor, cobarde y animoso;\n\nno hallar fuera del bien centro y reposo,\nmostrarse alegre, triste, humilde, altivo,\nenojado, valiente, fugitivo,\nsatisfecho, ofendido, receloso;\n\nhuir el rostro al claro desengaño,\nbeber veneno por licor süave,\nolvidar el provecho, amar el daño;\n\ncreer que un cielo en un infierno cabe,\ndar la vida y el alma a un desengaño:\nesto es amor, quien lo probó lo sabe."
    ),
    FormExample(
        id = "redondilla",
        name = "Redondilla",
        emoji = "🍂",
        structure = "4 versos octosílabos (8 sílabas)",
        rhymeScheme = "ABBA (rima abrazada)",
        author = "Sor Juana Inés de la Cruz",
        title = "Hombres necios",
        poem = "Hombres necios que acusáis\na la mujer sin razón,\nsin ver que sois la ocasión\nde lo mismo que culpáis."
    ),
    FormExample(
        id = "cuarteta",
        name = "Cuarteta",
        emoji = "🍃",
        structure = "4 versos octosílabos (8 sílabas)",
        rhymeScheme = "ABAB (rima cruzada)",
        author = "Antonio Machado",
        title = "Caminante",
        poem = "Caminante, son tus huellas\nel camino y nada más;\ncaminante, no hay camino,\nse hace camino al andar."
    ),
    FormExample(
        id = "decima",
        name = "Décima (Espinela)",
        emoji = "🎭",
        structure = "10 versos octosílabos (8 sílabas)",
        rhymeScheme = "ABBAACCDDC",
        author = "Vicente Espinel",
        title = "Décima ejemplo",
        poem = "Suele decirse, y con razón,\nque el tiempo todo lo vence,\nque al cabo al hombre le empieza\na quitar la ilusión.\nMas yo tengo convicción\nde que el amor verdadero,\nel que es firme y duradero,\nno cede ante el tiempo aquel;\nel amor es el cordel\nque une al humano al querer."
    ),
    FormExample(
        id = "libre",
        name = "Verso libre",
        emoji = "🌊",
        structure = "Sin métrica ni rima fijas. El ritmo surge del lenguaje",
        rhymeScheme = "Sin esquema obligatorio",
        author = "Pablo Neruda",
        title = "Puedo escribir",
        poem = "Puedo escribir los versos más tristes esta noche.\nEscribir, por ejemplo: «La noche está estrellada,\ny tiritan, azules, los astros, a lo lejos.»\n\nEl viento de la noche gira en el cielo y canta.\nPuedo escribir los versos más tristes esta noche.\nYo la quise, y a veces ella también me quería."
    ),
    FormExample(
        id = "romance",
        name = "Romance",
        emoji = "⚔️",
        structure = "Serie indefinida de versos octosílabos",
        rhymeScheme = "Rima asonante en los versos pares",
        author = "Anónimo (Romancero)",
        title = "Romance del prisionero",
        poem = "Que por mayo era por mayo\ncuando hace la calor,\ncuando los trigos encañan\ny están los campos en flor,\ncuando canta la calandria\ny responde el ruiseñor,\ncuando los enamorados\nvan a servir al amor;\nsino yo, triste cuitado,\nque vivo en esta prisión."
    )
)

// ─── Componentes ───────────────────────────────────────────────────────────

@Composable
private fun FormCard(
    form: FormExample,
    onUseExample: (title: String, poem: String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.SpacingSmall)
            .clickable { expanded = !expanded },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(Dimens.PaddingLarge)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = form.emoji,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = Dimens.PaddingMedium)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = form.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = form.structure,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Expandido: esquema + poema ejemplo
            if (expanded) {
                Spacer(Modifier.height(Dimens.PaddingMedium))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(Dimens.PaddingMedium))

                Text(
                    text = stringResource(R.string.forms_library_structure),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Rima: ${form.rhymeScheme}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(Dimens.PaddingMedium))

                Text(
                    text = stringResource(R.string.forms_library_example_poem),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "«${form.title}» — ${form.author}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(bottom = Dimens.PaddingSmall)
                )
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = form.poem,
                        fontFamily = PoeticFont,
                        fontSize = 16.sp,
                        lineHeight = 26.sp,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(Dimens.PaddingLarge)
                    )
                }
                Spacer(Modifier.height(Dimens.PaddingMedium))

                Button(
                    onClick = { onUseExample(form.title, form.poem) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(stringResource(R.string.forms_library_use_example))
                }
            }
        }
    }
}

// ─── Pantalla completa (Dialog fullscreen) ─────────────────────────────────

@Composable
fun FormsLibraryDialog(
    onDismiss: () -> Unit,
    onUseExample: (title: String, poem: String) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column {
                // Top bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.PaddingMedium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.forms_library_title),
                            fontFamily = PoeticFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                        Text(
                            text = stringResource(R.string.forms_library_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                HorizontalDivider()

                // Lista de formas
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(Dimens.PaddingLarge)
                ) {
                    items(FORM_EXAMPLES) { form ->
                        FormCard(
                            form = form,
                            onUseExample = { title, poem ->
                                onUseExample(title, poem)
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}
