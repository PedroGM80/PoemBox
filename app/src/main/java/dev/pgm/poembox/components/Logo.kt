package dev.pgm.poembox.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.pgm.poembox.R

@Composable
fun Logo() {
    val animateAlpha = remember { Animatable(0.1f) }
    LaunchedEffect(animateAlpha) {
        animateAlpha.animateTo(
            targetValue = 2f,
            animationSpec = repeatable(
                animation = tween(
                    durationMillis = 2000,
                    easing = LinearEasing,
                    delayMillis = 100
                ),
                repeatMode = RepeatMode.Restart,
                iterations = 3
            )
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,

        ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "My App Logo",
                modifier = Modifier
                    .alpha(animateAlpha.value)
                    .size(200.dp)
            )
            Text(
                text = "PoemBox",
                color = Color.Black,
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(animateAlpha.value)
                    .size(200.dp)
                    .padding(5.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrevLogo(){
    Logo()
}