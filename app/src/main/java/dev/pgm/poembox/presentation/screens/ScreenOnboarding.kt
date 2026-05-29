package dev.pgm.poembox.presentation.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.pgm.poembox.R
import dev.pgm.poembox.presentation.theme.Dimens
import dev.pgm.poembox.presentation.theme.PoeticFont
import dev.pgm.poembox.presentation.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

private data class OnboardingSlide(
    val icon: ImageVector,
    val titleRes: Int,
    val bodyRes: Int
)

private val slides = listOf(
    OnboardingSlide(Icons.Default.Create,       R.string.onboarding_slide1_title, R.string.onboarding_slide1_body),
    OnboardingSlide(Icons.Default.AutoStories,  R.string.onboarding_slide2_title, R.string.onboarding_slide2_body),
    OnboardingSlide(Icons.Default.Share,        R.string.onboarding_slide3_title, R.string.onboarding_slide3_body)
)

@Composable
fun ScreenOnboarding(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState { slides.size }
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == slides.lastIndex

    fun finish() {
        viewModel.completeOnboarding {
            navController.navigate(ScreensRouteList.RouteScreenCreateAccount.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Skip button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.PaddingLarge, vertical = Dimens.PaddingMedium),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = ::finish) {
                    Text(stringResource(R.string.onboarding_skip))
                }
            }

            // Slides
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                val slide = slides[page]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Dimens.PaddingExtraLarge),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(120.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = slide.icon,
                                contentDescription = null,
                                modifier = Modifier.size(56.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Spacer(Modifier.height(Dimens.PaddingExtraLarge))
                    Text(
                        text = stringResource(slide.titleRes),
                        fontFamily = PoeticFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(Dimens.PaddingLarge))
                    Text(
                        text = stringResource(slide.bodyRes),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 26.sp
                    )
                }
            }

            // Page indicators
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = Dimens.PaddingLarge)
            ) {
                repeat(slides.size) { index ->
                    val selected = pagerState.currentPage == index
                    val color by animateColorAsState(
                        targetValue = if (selected) MaterialTheme.colorScheme.primary
                                      else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                        label = "dot_color"
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = Dimens.SpacingSmall)
                            .size(if (selected) 10.dp else 6.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            // Navigation button
            Button(
                onClick = {
                    if (isLastPage) {
                        finish()
                    } else {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.PaddingExtraLarge)
                    .height(Dimens.ButtonHeight),
                shape = MaterialTheme.shapes.large
            ) {
                Text(
                    text = stringResource(if (isLastPage) R.string.onboarding_start else R.string.onboarding_next),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(Modifier.height(Dimens.PaddingExtraLarge))
        }
    }
}
