package dev.pgm.poembox.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.pgm.poembox.R
import dev.pgm.poembox.ui.theme.Shapes


@Composable
fun MonitoringScreen() {
    Surface(color = MaterialTheme.colors.primary) {
        Box(Modifier.wrapContentSize(Alignment.Center)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(id = R.color.white))
                    .align(Alignment.TopCenter)
            ) {

                Text(
                    text = "Analyze",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    fontSize = 25.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .background(Color.Yellow)
                        .clip(shape = Shapes.medium)
                )

                Row(Modifier.align(Alignment.CenterHorizontally)) {
                    Button(
                        onClick = { },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .width(200.dp)
                            .height(100.dp)
                            .padding(5.dp)
                    ) {
                        Text(
                            text = """Classification by
                            | number of syllables""".trimMargin()
                        )
                    }
                    Button(
                        onClick = { },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .width(200.dp)
                            .height(100.dp)
                            .padding(5.dp)
                    ) {
                        Text(
                            text = """Classification by
                            | number of verses""".trimMargin()
                        )
                    }
                }
                Row(Modifier.align(Alignment.CenterHorizontally)) {
                    Button(
                        onClick = { },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .width(200.dp)
                            .height(100.dp)
                            .padding(5.dp)
                    ) {
                        Text(
                            text = """Type of rhyme
                            | and meter""".trimMargin()
                        )
                    }
                    Button(
                        onClick = { },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .height(100.dp)
                            .width(200.dp)
                            .padding(5.dp)
                    ) {
                        Text(
                            text = """Enjambment
                            | or stichomytia""".trimMargin()
                        )
                    }
                }
                Row(Modifier.align(Alignment.CenterHorizontally)){
                    Button(
                        onClick = { },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(75.dp)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Validate poem",
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MonitoringScreenPreview() {
    MonitoringScreen()
}
