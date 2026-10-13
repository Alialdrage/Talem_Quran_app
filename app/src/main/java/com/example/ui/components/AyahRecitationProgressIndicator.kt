package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.PlaybackStatus
import com.example.audio.PlayerState
import com.example.data.model.Ayah
import com.example.data.model.Surah
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldPrimary

data class AyahPlaybackProgress(
    val ayah: Ayah,
    val ayahIndex: Int, // 0-based index
    val ayahNumber: Int, // 1-based number in Surah
    val totalAyahs: Int,
    val ayahStartMs: Int,
    val ayahDurationMs: Int,
    val elapsedInAyahMs: Int,
    val progressFraction: Float, // 0.0f .. 1.0f
    val nextAyahStartMs: Int?,
    val prevAyahStartMs: Int?
)

/**
 * دالة رياضية تحسب تقدم الآية الحالية بدقة بناءً على موقع التلاوة الحالي وزمن السورة
 */
fun calculateAyahProgress(
    surah: Surah,
    currentPositionMs: Int,
    totalDurationMs: Int
): AyahPlaybackProgress {
    val ayahs = surah.ayahs
    if (ayahs.isEmpty()) {
        val fallbackAyah = Ayah(numberInSurah = 1, text = surah.nameArabic)
        return AyahPlaybackProgress(
            ayah = fallbackAyah,
            ayahIndex = 0,
            ayahNumber = 1,
            totalAyahs = 1,
            ayahStartMs = 0,
            ayahDurationMs = 1,
            elapsedInAyahMs = 0,
            progressFraction = 0f,
            nextAyahStartMs = null,
            prevAyahStartMs = null
        )
    }

    val totalExpected = ayahs.sumOf { it.expectedDurationMs }.coerceAtLeast(1000L)
    val effectiveTotal = if (totalDurationMs > 0) totalDurationMs.toLong() else totalExpected

    val starts = ArrayList<Int>(ayahs.size)
    val durations = ArrayList<Int>(ayahs.size)
    var accumulated = 0.0

    for (ayah in ayahs) {
        starts.add(accumulated.toInt())
        val proportional = (ayah.expectedDurationMs.toDouble() / totalExpected.toDouble()) * effectiveTotal.toDouble()
        val durationInt = proportional.toInt().coerceAtLeast(1000)
        durations.add(durationInt)
        accumulated += proportional
    }

    var activeIdx = 0
    for (i in ayahs.indices) {
        val start = starts[i]
        val end = if (i + 1 < starts.size) starts[i + 1] else totalDurationMs.coerceAtLeast(start + durations[i])
        if (currentPositionMs >= start && (currentPositionMs < end || i == ayahs.lastIndex)) {
            activeIdx = i
            break
        }
    }
    activeIdx = activeIdx.coerceIn(0, ayahs.lastIndex)

    val currentAyah = ayahs[activeIdx]
    val startMs = starts[activeIdx]
    val durMs = durations[activeIdx]
    val elapsed = (currentPositionMs - startMs).coerceIn(0, durMs)
    val fraction = if (durMs > 0) (elapsed.toFloat() / durMs.toFloat()).coerceIn(0f, 1f) else 0f

    val prevStart = if (activeIdx > 0) starts[activeIdx - 1] else 0
    val nextStart = if (activeIdx + 1 < ayahs.size) starts[activeIdx + 1] else null

    return AyahPlaybackProgress(
        ayah = currentAyah,
        ayahIndex = activeIdx,
        ayahNumber = currentAyah.numberInSurah,
        totalAyahs = ayahs.size,
        ayahStartMs = startMs,
        ayahDurationMs = durMs,
        elapsedInAyahMs = elapsed,
        progressFraction = fraction,
        nextAyahStartMs = nextStart,
        prevAyahStartMs = if (activeIdx > 0) prevStart else null
    )
}

/**
 * مؤشر بصري أنيق يوضح تقدم التلاوة للآية الحالية مع نص الآية وموجات صوتية وشريط تقدم تفاعلي
 */
@Composable
fun AyahRecitationProgressIndicator(
    surah: Surah,
    playerState: PlayerState,
    onSeek: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isPlaying = playerState.status == PlaybackStatus.PLAYING
    val currentPositionMs = playerState.currentPositionMs
    val durationMs = playerState.durationMs

    val ayahProgress = remember(surah, currentPositionMs, durationMs) {
        calculateAyahProgress(surah, currentPositionMs, durationMs)
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = EmeraldPrimary.copy(alpha = 0.06f)
        ),
        border = BorderStroke(1.2.dp, GoldPrimary.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("ayah_recitation_progress_indicator")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row: Ayah badge, visual soundwave, and percentage badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ayah info pill
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = EmeraldPrimary.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.35f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = GoldDark,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "الآية الحالية: ${ayahProgress.ayahNumber} من ${ayahProgress.totalAyahs}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark
                        )
                    }
                }

                // Audio wave bars animation
                RecitationSoundwave(isPlaying = isPlaying)

                // Percentage inside current Ayah
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GoldPrimary.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, GoldDark.copy(alpha = 0.4f))
                ) {
                    val percent = (ayahProgress.progressFraction * 100).toInt()
                    Text(
                        text = "$percent% مكتمل",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Text of the Active Ayah with prominent Quranic styling
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.18f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "﴿ ${ayahProgress.ayah.text} ﴾",
                        fontSize = 17.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDark,
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("text_current_active_ayah")
                    )

                    if (ayahProgress.ayah.tajweedNote.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "💡 تنبيه التجويد: ${ayahProgress.ayah.tajweedNote}",
                            fontSize = 11.sp,
                            color = GoldDark,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Visual Progress Track for current Ayah
            Column(modifier = Modifier.fillMaxWidth()) {
                // Progress Bar with custom gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(EmeraldPrimary.copy(alpha = 0.15f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = ayahProgress.progressFraction)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(EmeraldPrimary, GoldPrimary)
                                )
                            )
                            .testTag("progress_bar_current_ayah")
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Time labels for current Ayah
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatDuration(ayahProgress.elapsedInAyahMs),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EmeraldPrimary
                    )

                    Text(
                        text = "تقدم تلاوة الآية",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = formatDuration(ayahProgress.ayahDurationMs),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Stepping between Ayahs and Verse Timeline Dots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous Ayah button
                Surface(
                    onClick = {
                        val prevMs = ayahProgress.prevAyahStartMs ?: 0
                        onSeek(prevMs)
                    },
                    enabled = ayahProgress.ayahIndex > 0,
                    shape = RoundedCornerShape(8.dp),
                    color = if (ayahProgress.ayahIndex > 0) Color.White else Color.Transparent,
                    border = BorderStroke(
                        1.dp,
                        if (ayahProgress.ayahIndex > 0) EmeraldPrimary.copy(alpha = 0.4f) else Color.Transparent
                    ),
                    modifier = Modifier.testTag("btn_prev_ayah")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "الآية السابقة",
                            tint = if (ayahProgress.ayahIndex > 0) EmeraldPrimary else Color.LightGray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "الآية السابقة",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (ayahProgress.ayahIndex > 0) EmeraldPrimary else Color.LightGray
                        )
                    }
                }

                // Next Ayah button
                Surface(
                    onClick = {
                        ayahProgress.nextAyahStartMs?.let { onSeek(it) }
                    },
                    enabled = ayahProgress.nextAyahStartMs != null,
                    shape = RoundedCornerShape(8.dp),
                    color = if (ayahProgress.nextAyahStartMs != null) Color.White else Color.Transparent,
                    border = BorderStroke(
                        1.dp,
                        if (ayahProgress.nextAyahStartMs != null) EmeraldPrimary.copy(alpha = 0.4f) else Color.Transparent
                    ),
                    modifier = Modifier.testTag("btn_next_ayah")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "الآية التالية",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (ayahProgress.nextAyahStartMs != null) EmeraldPrimary else Color.LightGray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "الآية التالية",
                            tint = if (ayahProgress.nextAyahStartMs != null) EmeraldPrimary else Color.LightGray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // If Surah has multiple Ayahs, show interactive Ayah dots
            if (surah.ayahs.size in 2..20) {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(surah.ayahs) { index, verse ->
                        val isCurrent = index == ayahProgress.ayahIndex
                        val isCompleted = index < ayahProgress.ayahIndex

                        Surface(
                            onClick = {
                                // Jump directly to this ayah's approximate start time
                                val targetExpected = surah.ayahs.take(index).sumOf { it.expectedDurationMs }.toDouble()
                                val totalExp = surah.ayahs.sumOf { it.expectedDurationMs }.coerceAtLeast(1000L).toDouble()
                                val effectiveTot = if (durationMs > 0) durationMs.toDouble() else totalExp
                                val targetMs = ((targetExpected / totalExp) * effectiveTot).toInt()
                                onSeek(targetMs)
                            },
                            shape = CircleShape,
                            color = when {
                                isCurrent -> GoldPrimary
                                isCompleted -> EmeraldPrimary
                                else -> MaterialTheme.colorScheme.surface
                            },
                            border = BorderStroke(
                                1.dp,
                                when {
                                    isCurrent -> GoldDark
                                    isCompleted -> EmeraldDark
                                    else -> MaterialTheme.colorScheme.outlineVariant
                                }
                            ),
                            modifier = Modifier
                                .size(if (isCurrent) 26.dp else 22.dp)
                                .testTag("chip_ayah_step_${verse.numberInSurah}")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${verse.numberInSurah}",
                                    fontSize = if (isCurrent) 10.5.sp else 9.5.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                    color = when {
                                        isCurrent -> Color.White
                                        isCompleted -> Color.White
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * موجات صوتية نابضة تعطي انطباعاً حياً بحركة التلاوة وعذوبتها
 */
@Composable
fun RecitationSoundwave(
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "soundwave_pulse")

    val h1 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = if (isPlaying) 1.0f else 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar1"
    )
    val h2 by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = if (isPlaying) 0.9f else 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, delayMillis = 100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar2"
    )
    val h3 by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = if (isPlaying) 1.0f else 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(480, delayMillis = 50, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar3"
    )
    val h4 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = if (isPlaying) 0.85f else 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(520, delayMillis = 150, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar4"
    )

    Row(
        modifier = modifier
            .height(18.dp)
            .padding(horizontal = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(h1, h2, h3, h4, h2).forEachIndexed { idx, heightFactor ->
            val barHeight = (4.dp + (14.dp * heightFactor))
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(barHeight)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(
                        if (isPlaying) {
                            if (idx % 2 == 0) EmeraldPrimary else GoldPrimary
                        } else {
                            Color.Gray.copy(alpha = 0.4f)
                        }
                    )
            )
        }
    }
}
