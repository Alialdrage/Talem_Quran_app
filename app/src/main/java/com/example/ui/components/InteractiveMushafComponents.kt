package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Ayah
import com.example.data.model.Surah
import com.example.data.model.TafsirBook
import com.example.data.model.TajweedCategory
import com.example.data.model.TajweedRule
import com.example.ui.theme.DarkVerseCardBg
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.VerseCardBg
import com.example.ui.theme.VerseCardBorder

/**
 * نمط عرض المصحف التفاعلي:
 * صفحة المصحف الشريف المتصل كالمصحف الورقي، أو نمط البطاقات المفصلة.
 */
enum class MushafDisplayMode(val title: String, val shortTitle: String) {
    CONTINUOUS_PAGE("صفحة المصحف المتصل 📖", "المصحف الشريف"),
    DETAILED_CARDS("بطاقات الآيات المفصلة 🗂️", "بطاقات الآيات")
}

/**
 * شريط التحكم العلوي بنمط المصحف التفاعلي، الانتقال السريع بالآيات، والفاصل المرجعي
 */
@Composable
fun MushafTopInteractiveBar(
    currentMode: MushafDisplayMode,
    onModeChange: (MushafDisplayMode) -> Unit,
    bookmarkedAyahNumber: Int?,
    bookmarkedSurahId: String?,
    currentSurah: Surah,
    onJumpToBookmark: () -> Unit,
    onOpenAyahPicker: () -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSearchBar by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
    ) {
        // شريط التبديل بين المصحف الشريف المتصل وبطاقات الآيات
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Segmented mode switcher
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Row(modifier = Modifier.padding(3.dp)) {
                    MushafDisplayMode.values().forEach { mode ->
                        val isSelected = currentMode == mode
                        Surface(
                            onClick = { onModeChange(mode) },
                            shape = RoundedCornerShape(9.dp),
                            color = if (isSelected) EmeraldPrimary else Color.Transparent,
                            border = if (isSelected) BorderStroke(1.dp, EmeraldDark) else null,
                            modifier = Modifier.testTag("btn_mushaf_mode_${mode.name.lowercase()}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = if (mode == MushafDisplayMode.CONTINUOUS_PAGE) Icons.Default.MenuBook else Icons.Default.GridView,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = mode.shortTitle,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // أزرار الانتقال السريع للآية والبحث
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                // زر الانتقال برقم الآية
                Surface(
                    onClick = onOpenAyahPicker,
                    shape = RoundedCornerShape(10.dp),
                    color = GoldPrimary.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                    modifier = Modifier.testTag("btn_jump_to_ayah_picker")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = "انتقال لآية",
                            tint = GoldDark,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "انتقال ﴿...﴾",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldDark
                        )
                    }
                }

                // زر البحث في آيات السورة
                Surface(
                    onClick = { showSearchBar = !showSearchBar },
                    shape = RoundedCornerShape(10.dp),
                    color = if (showSearchBar || searchQuery.isNotBlank()) EmeraldPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = BorderStroke(1.dp, if (showSearchBar || searchQuery.isNotBlank()) EmeraldPrimary else MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.testTag("btn_toggle_surah_search")
                ) {
                    Box(
                        modifier = Modifier.padding(7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "بحث في السورة",
                            tint = if (showSearchBar || searchQuery.isNotBlank()) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }

        // شريط البحث المنسدل عند التفعيل
        AnimatedVisibility(
            visible = showSearchBar,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_mushaf_search_query"),
                    placeholder = {
                        Text("ابحث عن أي كلمة أو رقم آية في ${currentSurah.nameArabic}...", fontSize = 12.sp)
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "مسح", tint = Color.Gray, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
            }
        }

        // بطاقة فاصل القراءة المحفوظ (Bookmark)
        if (bookmarkedAyahNumber != null && bookmarkedSurahId == currentSurah.id) {
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                onClick = onJumpToBookmark,
                shape = RoundedCornerShape(10.dp),
                color = GoldPrimary.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.35f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("banner_saved_reading_bookmark")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "فاصل القراءة",
                            tint = GoldDark,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "فاصل القراءة المحفوظ: الآية ($bookmarkedAyahNumber)",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldDark
                        )
                    }

                    Text(
                        text = "انتقال وقراءة ↗",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                }
            }
        }
    }
}

/**
 * صفحة المصحف الشريف المتصل (Continuous Mushaf Page)
 * إطار إسلامي مزخرف ومذهب، ترويسة السورة، بسملة، وآيات متدفقة متصلة مع رموز أرقام الآيات
 * يمكن الضغط على أي آية لفتح خيارات التفاعل الفوري (استماع، تكرار، تفسير، تجويد، فاصل).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ContinuousMushafPageView(
    surah: Surah,
    isNightMode: Boolean,
    fontSizeSp: Int,
    isTajweedEnabled: Boolean,
    activeRecitingAyahNumber: Int?,
    selectedInteractiveAyahNumber: Int?,
    bookmarkedAyahNumber: Int?,
    searchQuery: String,
    onSelectAyah: (Ayah) -> Unit,
    onSelectTajweedRule: (TajweedRule) -> Unit,
    modifier: Modifier = Modifier
) {
    val pageBgColor = if (isNightMode) DarkVerseCardBg else Color(0xFFFCFAF2)
    val outerBorderColor = if (isNightMode) GoldDark.copy(alpha = 0.6f) else GoldPrimary
    val innerBorderColor = if (isNightMode) EmeraldLight.copy(alpha = 0.3f) else GoldLight.copy(alpha = 0.4f)
    val textColor = if (isNightMode) Color(0xFFE8ECE9) else Color(0xFF1E281F)

    // تصفية الآيات حسب البحث إن وجد
    val filteredAyahs = remember(surah.ayahs, searchQuery) {
        if (searchQuery.isBlank()) {
            surah.ayahs
        } else {
            val queryClean = searchQuery.trim()
            surah.ayahs.filter { ayah ->
                ayah.text.contains(queryClean) ||
                ayah.numberInSurah.toString() == queryClean ||
                ayah.tafsir.contains(queryClean)
            }
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = pageBgColor),
        border = BorderStroke(2.dp, outerBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("continuous_mushaf_page_card")
    ) {
        // الإطار الداخلي المذهب ذو الطابع الإسلامي
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
                .border(1.dp, innerBorderColor, RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ترويسة اسم السورة المزخرفة (Surah Placard)
                SurahHeaderPlacard(surah = surah, isNightMode = isNightMode)

                Spacer(modifier = Modifier.height(10.dp))

                // البسملة الشريفة
                if (surah.hasBasmalah) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                            fontSize = (fontSizeSp - 2).coerceAtLeast(16).sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isNightMode) GoldLight else GoldDark,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // تنبيه تعليمي تفاعلي
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldPrimary.copy(alpha = if (isNightMode) 0.15f else 0.08f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Text(
                        text = "💡 اضغط على أي آية أو رقم للاستماع، التكرار، التفسير، أو حفظ فاصل القراءة",
                        fontSize = 11.sp,
                        color = if (isNightMode) EmeraldLight else EmeraldPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // سيل الآيات المتصل (Continuous Ayahs Flow)
                FlowRow(
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("continuous_mushaf_ayahs_container")
                ) {
                    filteredAyahs.forEach { ayah ->
                        val isReciting = activeRecitingAyahNumber == ayah.numberInSurah
                        val isSelected = selectedInteractiveAyahNumber == ayah.numberInSurah
                        val isBookmarked = bookmarkedAyahNumber == ayah.numberInSurah

                        // شريحة الآية التفاعلية داخل المصحف المتصل
                        Surface(
                            onClick = { onSelectAyah(ayah) },
                            shape = RoundedCornerShape(10.dp),
                            color = when {
                                isReciting -> GoldPrimary.copy(alpha = if (isNightMode) 0.35f else 0.22f)
                                isSelected -> EmeraldPrimary.copy(alpha = if (isNightMode) 0.28f else 0.14f)
                                isBookmarked -> GoldPrimary.copy(alpha = 0.10f)
                                else -> Color.Transparent
                            },
                            border = when {
                                isReciting -> BorderStroke(1.5.dp, GoldPrimary)
                                isSelected -> BorderStroke(1.2.dp, EmeraldPrimary)
                                isBookmarked -> BorderStroke(1.dp, GoldDark.copy(alpha = 0.5f))
                                else -> null
                            },
                            modifier = Modifier
                                .padding(horizontal = 2.dp, vertical = 2.dp)
                                .testTag("mushaf_ayah_${ayah.numberInSurah}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                // نص الآية مع ألوان التجويد إن كانت مفعلة
                                if (isTajweedEnabled && ayah.segments.isNotEmpty()) {
                                    InteractiveAyahTextFlow(
                                        ayah = ayah,
                                        isNightMode = isNightMode,
                                        fontSizeSp = fontSizeSp,
                                        onSelectRule = onSelectTajweedRule
                                    )
                                } else {
                                    Text(
                                        text = ayah.text,
                                        fontSize = fontSizeSp.sp,
                                        lineHeight = (fontSizeSp + 10).sp,
                                        fontWeight = FontWeight.Medium,
                                        color = textColor,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                Spacer(modifier = Modifier.width(4.dp))

                                // رقم الآية داخل الشارة المزخرفة ﴿١﴾
                                AyahEndBadge(
                                    ayahNumber = ayah.numberInSurah,
                                    isReciting = isReciting,
                                    isBookmarked = isBookmarked,
                                    isNightMode = isNightMode
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
 * شارة ترويسة السورة ذات الطابع الإسلامي المزخرف
 */
@Composable
fun SurahHeaderPlacard(surah: Surah, isNightMode: Boolean) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isNightMode) Color(0xFF1E2F23) else Color(0xFFF4EDE0),
        border = BorderStroke(1.5.dp, if (isNightMode) GoldDark else GoldPrimary),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("surah_header_placard")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // مكان النزول
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = EmeraldPrimary.copy(alpha = 0.12f)
            ) {
                Text(
                    text = surah.revelationType,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isNightMode) EmeraldLight else EmeraldPrimary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }

            // اسم السورة في الوسط
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "۞",
                    fontSize = 13.sp,
                    color = GoldDark
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = surah.nameArabic,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isNightMode) GoldLight else EmeraldDark
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "۞",
                    fontSize = 13.sp,
                    color = GoldDark
                )
            }

            // عدد الآيات
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = GoldPrimary.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "${surah.versesCount} آيات",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldDark,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }
    }
}

/**
 * شارة رقم الآية المزخرفة ﴿١﴾ مع دعم علامة الحفظ أو التلاوة الحالية
 */
@Composable
fun AyahEndBadge(
    ayahNumber: Int,
    isReciting: Boolean,
    isBookmarked: Boolean,
    isNightMode: Boolean
) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(
                when {
                    isReciting -> GoldPrimary
                    isBookmarked -> GoldPrimary.copy(alpha = 0.25f)
                    else -> if (isNightMode) Color(0xFF26382A) else Color(0xFFEBE0CD)
                }
            )
            .border(
                1.dp,
                if (isReciting || isBookmarked) GoldDark else GoldPrimary.copy(alpha = 0.6f),
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isReciting) {
            Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = "تتلى الآن",
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        } else {
            Text(
                text = "$ayahNumber",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isReciting) Color.White else if (isNightMode) GoldLight else EmeraldDark
            )
        }
    }
}

/**
 * تفكيك وعرض كلمات الآية في المصحف المتصل مع ألوان أحكام التجويد
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InteractiveAyahTextFlow(
    ayah: Ayah,
    isNightMode: Boolean,
    fontSizeSp: Int,
    onSelectRule: (TajweedRule) -> Unit
) {
    val parts = remember(ayah.text, ayah.segments) {
        parseAyahTextSegments(ayah.text, ayah.segments)
    }

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        parts.forEach { part ->
            if (part.rule != null) {
                val categoryColor = Color(part.rule.category.colorHex)
                Text(
                    text = part.text,
                    fontSize = fontSizeSp.sp,
                    fontWeight = FontWeight.Bold,
                    color = categoryColor,
                    modifier = Modifier
                        .clickable { onSelectRule(part.rule) }
                        .padding(horizontal = 2.dp)
                )
            } else {
                Text(
                    text = part.text,
                    fontSize = fontSizeSp.sp,
                    fontWeight = FontWeight.Normal,
                    color = if (isNightMode) Color(0xFFE8ECE9) else Color(0xFF1E281F)
                )
            }
        }
    }
}

/**
 * لوحة إجراءات الآية التفاعلية الشاملة (Interactive Ayah Action Dialog):
 * - الاستماع الفوري للآية بصوت القارئ
 * - تكرار الآية للحفظ
 * - التفسير الشامل (تفسير التبيان للشيخ الطوسي، الميزان للطباطبائي، مجمع البيان، الأمثل، الميسر، والترجمة الإنجليزية)
 * - استعراض أحكام التجويد للآية
 * - وضع فاصل القراءة المرجعي
 * - نسخ الآية الشريفة
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InteractiveAyahActionDialog(
    surah: Surah,
    ayah: Ayah,
    isNightMode: Boolean,
    isReciting: Boolean,
    isLooping: Boolean,
    isBookmarked: Boolean,
    selectedTafsirBook: TafsirBook,
    onSelectTafsirBook: (TafsirBook) -> Unit,
    onPlayAyahAudio: () -> Unit,
    onToggleAyahLoop: () -> Unit,
    onBookmarkAyah: () -> Unit,
    onSelectTajweedRule: (TajweedRule) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var activeTab by remember { mutableStateOf(0) } // 0: التفاسير, 1: أحكام التجويد, 2: الترجمة

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isNightMode) DarkVerseCardBg else Color(0xFFFCFAF4)
            ),
            border = BorderStroke(1.5.dp, GoldPrimary),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 16.dp)
                .testTag("interactive_ayah_action_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // رأس النافذة: اسم السورة ورقم الآية مع زر الإغلاق
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldPrimary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "${surah.nameArabic} • الآية (${ayah.numberInSurah})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp).testTag("btn_close_ayah_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // بطاقة نص الآية الشريفة بخط واضح وكبير
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isNightMode) Color(0xFF1E2E22) else Color(0xFFF7F2E7),
                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = ayah.text,
                            fontSize = 20.sp,
                            lineHeight = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isNightMode) Color(0xFFF0F5F1) else Color(0xFF1D281E),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // شريط أزرار الإجراءات التفاعلية (استماع، تكرار، فاصل، نسخ)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // زر تشغيل الآية 🔊
                    Surface(
                        onClick = onPlayAyahAudio,
                        shape = RoundedCornerShape(10.dp),
                        color = if (isReciting) GoldPrimary else EmeraldPrimary,
                        modifier = Modifier.weight(1f).testTag("btn_dialog_play_ayah")
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = if (isReciting) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isReciting) "إيقاف المؤقت" else "استمع للآية",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // زر تكرار الآية 🔁 للحفظ
                    Surface(
                        onClick = onToggleAyahLoop,
                        shape = RoundedCornerShape(10.dp),
                        color = if (isLooping) GoldPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, if (isLooping) GoldPrimary else MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.weight(0.9f).testTag("btn_dialog_loop_ayah")
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Repeat,
                                contentDescription = null,
                                tint = if (isLooping) GoldDark else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isLooping) "تكرار: مفعّل" else "تكرار الحفظ",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isLooping) GoldDark else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // زر حفظ فاصل القراءة 🔖
                    Surface(
                        onClick = {
                            onBookmarkAyah()
                            Toast.makeText(context, "تم حفظ فاصل القراءة عند الآية (${ayah.numberInSurah})", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isBookmarked) GoldPrimary.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = BorderStroke(1.dp, if (isBookmarked) GoldPrimary else MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.weight(0.8f).testTag("btn_dialog_bookmark_ayah")
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                tint = if (isBookmarked) GoldDark else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (isBookmarked) "محفوظ" else "فاصل",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isBookmarked) GoldDark else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // زر نسخ الآية 📋
                    Surface(
                        onClick = {
                            val copyText = "﴿${ayah.text}﴾ [سورة ${surah.nameArabic}: ${ayah.numberInSurah}]"
                            clipboardManager.setText(AnnotatedString(copyText))
                            Toast.makeText(context, "تم نسخ الآية إلى الحافظة", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.weight(0.7f).testTag("btn_dialog_copy_ayah")
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "نسخ",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "نسخ",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // تبويبات التفاصيل: [التفسير القرآني] • [أحكام التجويد] • [الترجمة الإنجليزية]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("التفاسير المعتمدة 📖", "أحكام التجويد ✨", "English Translation").forEachIndexed { index, label ->
                        val isTabSelected = activeTab == index
                        Surface(
                            onClick = { activeTab = index },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isTabSelected) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            border = if (isTabSelected) BorderStroke(1.dp, EmeraldDark) else null,
                            modifier = Modifier.weight(1f).testTag("dialog_tab_$index")
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.5.sp,
                                fontWeight = if (isTabSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isTabSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // محتوى التبويب المختار
                when (activeTab) {
                    0 -> {
                        // تبويب التفاسير المعتمدة (مع دعم كامل لتفسير التبيان لشيخ الطائفة الطوسي)
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // أزرار اختيار كتاب التفسير
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                            ) {
                                TafsirBook.values().forEach { book ->
                                    val isBookSelected = selectedTafsirBook == book
                                    Surface(
                                        onClick = { onSelectTafsirBook(book) },
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isBookSelected) EmeraldPrimary else Color.White,
                                        border = BorderStroke(1.dp, if (isBookSelected) EmeraldDark else EmeraldPrimary.copy(alpha = 0.3f)),
                                        modifier = Modifier.testTag("dialog_tafsir_btn_${book.id}")
                                    ) {
                                        Text(
                                            text = when (book) {
                                                TafsirBook.AL_MIZAN -> "الميزان"
                                                TafsirBook.AL_TIBYAN -> "التبيان (الطوسي)"
                                                TafsirBook.MAJMA_AL_BAYAN -> "مجمع البيان"
                                                TafsirBook.AL_AMTHAL -> "الأمثل"
                                                TafsirBook.MUYASSAR -> "الميسر"
                                            },
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isBookSelected) Color.White else EmeraldDark,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }

                            // شارة عنوان التفسير والمؤلف
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            ) {
                                Text(
                                    text = "${selectedTafsirBook.title} • ${selectedTafsirBook.author}",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // نص التفسير المختار
                            val currentTafsirText = when (selectedTafsirBook) {
                                TafsirBook.AL_MIZAN -> ayah.tafsirAlMizan.ifBlank {
                                    "في «الميزان في تفسير القرآن» للعلامة السيد محمد حسين الطباطبائي (قدس سره): بيان قرآني تحليلي رصين يفسر القرآن بالقرآن، مستنطقاً الآيات الكريمة في إيضاح معاني التوحيد والهداية الإلهية والحقائق الروحية والتشريعية في الآية (${ayah.numberInSurah})."
                                }
                                TafsirBook.AL_TIBYAN -> ayah.tafsirAlTibyan.ifBlank {
                                    "في «التبيان في تفسير القرآن» لشيخ الطائفة أبي جعفر محمد بن الحسن الطوسي (قدس سره): بيان لغوي ونحوي وبلاغي محكم لمعاني الكلمات ودلالات الآية الكريمة، مع استعراض وجوه القراءات والأحكام الفقهية والمقاصد الإيمانية الواردة في الآية (${ayah.numberInSurah})."
                                }
                                TafsirBook.MAJMA_AL_BAYAN -> ayah.tafsirMajmaAlBayan.ifBlank { ayah.tafsirAlAmthal.ifBlank { ayah.tafsir } }
                                TafsirBook.AL_AMTHAL -> ayah.tafsirAlAmthal.ifBlank { ayah.tafsir }
                                TafsirBook.MUYASSAR -> ayah.tafsir.ifBlank { ayah.tafsirAlAmthal }
                            }

                            Text(
                                text = currentTafsirText.ifBlank { "تفسير جليل يوضح معاني كلمات الآية الكريمة وأبعادها الهداية والإيمانية." },
                                fontSize = 12.5.sp,
                                lineHeight = 20.sp,
                                color = if (isNightMode) Color(0xFFD6DFD8) else Color(0xFF1E281F)
                            )
                        }
                    }

                    1 -> {
                        // تبويب أحكام التجويد في الآية
                        if (ayah.segments.isNotEmpty()) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                ayah.segments.forEach { rule ->
                                    val categoryColor = Color(rule.category.colorHex)
                                    Surface(
                                        onClick = {
                                            onDismiss()
                                            onSelectTajweedRule(rule)
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        color = categoryColor.copy(alpha = 0.12f),
                                        border = BorderStroke(1.dp, categoryColor.copy(alpha = 0.6f)),
                                        modifier = Modifier.testTag("dialog_segment_${rule.segmentText}")
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(categoryColor)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = rule.segmentText,
                                                fontSize = 12.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isNightMode) Color.White else Color(0xFF1C271E)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = categoryColor.copy(alpha = 0.2f)
                                            ) {
                                                Text(
                                                    text = rule.category.badgeName,
                                                    fontSize = 9.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = categoryColor,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = "تلاوة عامة بأحكام الترتيل ومخارج الحروف الصحيحة.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    2 -> {
                        // تبويب الترجمة الإنجليزية
                        if (ayah.englishTranslation.isNotBlank()) {
                            Text(
                                text = ayah.englishTranslation,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = if (isNightMode) Color(0xFFC0CEC3) else Color(0xFF374151)
                            )
                        } else {
                            Text(
                                text = "English translation is available in full surah index.",
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * نافذة سريعة للاختيار المباشر لرقم الآية (Ayah Picker Dialog)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AyahQuickPickerDialog(
    surah: Surah,
    onSelectAyah: (Ayah) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "انتقال إلى آية في ${surah.nameArabic}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "اختر رقم الآية للانتقال المباشر إليها (إجمالي ${surah.versesCount} آيات):",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    surah.ayahs.forEach { ayah ->
                        Surface(
                            onClick = {
                                onSelectAyah(ayah)
                                onDismiss()
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldPrimary.copy(alpha = 0.1f),
                            border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.35f)),
                            modifier = Modifier.size(38.dp).testTag("picker_ayah_${ayah.numberInSurah}")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${ayah.numberInSurah}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = EmeraldPrimary)
            }
        }
    )
}
