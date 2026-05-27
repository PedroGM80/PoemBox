package dev.pgm.poembox.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.pgm.poembox.R

import dev.pgm.poembox.domain.Constants
import dev.pgm.poembox.presentation.theme.Dimens

@Composable
fun Logo() {
    val animateAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animateAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = Constants.ANIMATION_DURATION_LONG,
                easing = FastOutSlowInEasing
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(animateAlpha.value)
        ) {
            Image(
                painter = painterResource(id = R.drawable.splash),
                contentDescription = stringResource(R.string.logo_content_description),
                modifier = Modifier.size(Dimens.LogoSize)
            )
            Spacer(modifier = Modifier.height(Dimens.PaddingLarge))
            Text(
                text = stringResource(R.string.brand_name),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.PaddingLarge)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrevLogo() {
    Logo()
}
