package ltd.kyss.petme.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import kotlinx.coroutines.delay

/**
 * Точка подключения артов. Если PNG с таким именем есть в drawable-nodpi,
 * он показывается автоматически. До поставки арта остаётся текстовая заглушка.
 */
@Composable
fun GameArt(
    assetName: String,
    fallbackEmoji: String,
    modifier: Modifier = Modifier,
    fallbackSize: TextUnit = 40.sp,
    flipHorizontally: Boolean = false,
    contentScale: ContentScale = ContentScale.Fit,
    contentDescription: String? = null
) {
    val resourceId = drawableId(assetName)
    val flip = if (flipHorizontally) -1f else 1f

    if (resourceId != 0) {
        Image(
            painter = painterResource(resourceId),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier.graphicsLayer(scaleX = flip)
        )
    } else {
        Box(
            modifier = modifier.graphicsLayer(scaleX = flip),
            contentAlignment = Alignment.Center
        ) {
            Text(fallbackEmoji, fontSize = fallbackSize)
        }
    }
}

/**
 * Покадровая анимация из файлов prefix_01.png ... prefix_06.png.
 * Если хотя бы одного кадра нет, используется статичный арт или эмодзи.
 */
@Composable
fun AnimatedGameArt(
    animationPrefix: String,
    staticAssetName: String,
    fallbackEmoji: String,
    isAnimating: Boolean,
    modifier: Modifier = Modifier,
    frameCount: Int = 6,
    frameDurationMillis: Long = 90L,
    fallbackSize: TextUnit = 48.sp,
    flipHorizontally: Boolean = false,
    contentDescription: String? = null
) {
    val frameIds = (1..frameCount).map { index ->
        drawableId("${animationPrefix}_${index.toString().padStart(2, '0')}")
    }
    val hasFullAnimation = frameIds.all { it != 0 }
    var frameIndex by remember(animationPrefix) { mutableIntStateOf(0) }

    LaunchedEffect(isAnimating, animationPrefix, hasFullAnimation) {
        frameIndex = 0
        while (isAnimating && hasFullAnimation) {
            delay(frameDurationMillis)
            frameIndex = (frameIndex + 1) % frameCount
        }
    }

    if (isAnimating && hasFullAnimation) {
        Image(
            painter = painterResource(frameIds[frameIndex]),
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = modifier.graphicsLayer(scaleX = if (flipHorizontally) -1f else 1f)
        )
    } else {
        GameArt(
            assetName = staticAssetName,
            fallbackEmoji = fallbackEmoji,
            modifier = modifier,
            fallbackSize = fallbackSize,
            flipHorizontally = flipHorizontally,
            contentDescription = contentDescription
        )
    }
}

@Composable
@SuppressLint("DiscouragedApi")
private fun drawableId(assetName: String): Int {
    val context = LocalContext.current
    return remember(assetName, context) {
        context.resources.getIdentifier(assetName, "drawable", context.packageName)
    }
}
