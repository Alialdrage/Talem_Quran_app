package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Surah
import com.example.ui.components.AppHeader
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary

/**
 * Returns the traditional Quranic Juz name or range for a given Surah number.
 */
fun getSurahJuzName(surahNumber: Int): String {
    return when (surahNumber) {
        1, 2 -> "الجزء 1 - 3"
        3 -> "الجزء 3 - 4"
        4 -> "الجزء 4 - 6"
        5 -> "الجزء 6 - 7"
        6 -> "الجزء 7 - 8"
        7 -> "الجزء 8 - 9"
        8 -> "الجزء 9 - 10"
        9 -> "الجزء 10 - 11"
        10, 11 -> "الجزء 11 - 12"
        12 -> "الجزء 12 - 13"
        13, 14 -> "الجزء 13"
        15, 16 -> "الجزء 14"
        17, 18 -> "الجزء 15 - 16"
        19, 20 -> "الجزء 16"
        21, 22 -> "الجزء 17"
        23, 24 -> "الجزء 18"
        25, 26, 27 -> "الجزء 19"
        28, 29 -> "الجزء 20"
        30, 31, 32, 33 -> "الجزء 21"
        34, 35, 36 -> "الجزء 22"
        37, 38, 39 -> "الجزء 23"
        40, 41 -> "الجزء 24"
        42, 43, 44, 45 -> "الجزء 25"
        46, 47, 48, 49, 50, 51 -> "الجزء 26"
        52, 53, 54, 55, 56, 57 -> "الجزء 27"
        in 58..66 -> "الجزء 28 (قد سمع)"
        in 67..77 -> "الجزء 29 (تبارك)"
        in 78..114 -> "الجزء 30 (عمّ)"
        else -> "الجزء 1"
    }
}

/**
 * Converts Western digits to Eastern Arabic numerals.
 */
fun toArabicDigits(number: Int): String {
    val arabicNumerals = arrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    return number.toString().map { char ->
        if (char in '0'..'9') arabicNumerals[char - '0'] else char
    }.joinToString("")
}

@Composable
fun SurahsScreen(
    surahs: List<Surah>,
    selectedSurahId: String,
    isFavorite: Boolean,
    onSelectSurah: (Surah) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onNavigateToPractice: (Surah) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("الكل") }
    var selectedJuzFilter by remember { mutableStateOf("جميع الأجزاء") }

    val currentlySelectedSurah = remember(surahs, selectedSurahId) {
        surahs.firstOrNull { it.id == selectedSurahId } ?: surahs.firstOrNull()
    }

    val filteredSurahs = remember(searchQuery, selectedFilter, selectedJuzFilter, surahs) {
        surahs.filter { surah ->
            val matchesQuery = searchQuery.isBlank() ||
                    surah.nameArabic.contains(searchQuery.trim(), ignoreCase = true) ||
                    surah.nameEnglish.contains(searchQuery.trim(), ignoreCase = true) ||
                    surah.number.toString().contains(searchQuery.trim()) ||
                    toArabicDigits(surah.number).contains(searchQuery.trim())

            val matchesFilter = when (selectedFilter) {
                "مدنية" -> surah.revelationType.contains("مدنية")
                "مكية" -> surah.revelationType.contains("مكية")
                else -> true
            }

            val matchesJuz = when (selectedJuzFilter) {
                "جزء عمّ (30)" -> surah.number in 78..114
                "جزء تبارك (29)" -> surah.number in 67..77
                "جزء قد سمع (28)" -> surah.number in 58..66
                "السبع الطوال (1-9)" -> surah.number in 1..9
                else -> true
            }

            matchesQuery && matchesFilter && matchesJuz
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App header
        AppHeader(
            title = "فهرس سور القرآن الكريم",
            subtitle = "١١٤ سورة مباركة كاملة — اضغط على أي سورة للانتقال لشاشة القراءة"
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Visual Banner & Quran Overview Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.25f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("surahs_hero_card")
                ) {
                    Column {
                        // Decorative Header Image Banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(115.dp)
                                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.quran_banner_1790277090612),
                                contentDescription = "زخرفة القرآن الكريم",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            // Gradient Overlay for readability
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Black.copy(alpha = 0.35f),
                                                Color.Black.copy(alpha = 0.65f)
                                            )
                                        )
                                    )
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldLight,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "كِتَابٌ أَنزَلْنَاهُ إِلَيْكَ مُبَارَكٌ لِّيَدَّبَّرُوا آيَاتِهِ",
                                    fontSize = 12.5.sp,
                                    color = Color.White.copy(alpha = 0.95f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Summary Statistics Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatPill(title = "السور", value = "١١٤", color = EmeraldPrimary)
                            Box(
                                modifier = Modifier
                                    .height(24.dp)
                                    .width(1.dp)
                                    .background(MaterialTheme.colorScheme.outlineVariant)
                            )
                            StatPill(title = "الآيات", value = "٦٢٣٦", color = GoldDark)
                            Box(
                                modifier = Modifier
                                    .height(24.dp)
                                    .width(1.dp)
                                    .background(MaterialTheme.colorScheme.outlineVariant)
                            )
                            StatPill(title = "الأجزاء", value = "٣٠ جزءاً", color = Color(0xFF0277BD))
                            Box(
                                modifier = Modifier
                                    .height(24.dp)
                                    .width(1.dp)
                                    .background(MaterialTheme.colorScheme.outlineVariant)
                            )
                            StatPill(title = "الأحزاب", value = "٦٠ حزباً", color = Color(0xFF6A1B9A))
                        }
                    }
                }
            }

            // Quick Resume Card (Last / Currently Selected Surah)
            if (currentlySelectedSurah != null) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = EmeraldPrimary.copy(alpha = 0.08f)
                        ),
                        border = BorderStroke(1.5.dp, EmeraldPrimary.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectSurah(currentlySelectedSurah) }
                            .testTag("resume_reading_card")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MenuBook,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "متابعة القراءة",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldPrimary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = GoldPrimary.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "آخر سورة",
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = GoldDark,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "سورة ${currentlySelectedSurah.nameArabic}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${currentlySelectedSurah.revelationType} • ${currentlySelectedSurah.versesCount} آية • ${getSurahJuzName(currentlySelectedSurah.number)}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Button(
                                onClick = { onSelectSurah(currentlySelectedSurah) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EmeraldPrimary,
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                modifier = Modifier.testTag("btn_resume_reading")
                            ) {
                                Text(
                                    text = "اقرأ الآن",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "ابحث بالاسم أو الرقم (الفاتحة، البقرة، الكهف، يس، 36)...",
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "بحث",
                            tint = EmeraldPrimary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "مسح البحث",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_search_surah")
                )
            }

            // Filter Chips Row
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Type Filters (All, Makki, Madani)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "النوع:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        listOf(
                            "الكل" to "١١٤ سورة",
                            "مكية" to "٨٦ سورة 🕋",
                            "مدنية" to "٢٨ سورة 🕌"
                        ).forEach { (filterKey, label) ->
                            FilterChip(
                                selected = selectedFilter == filterKey,
                                onClick = { selectedFilter = filterKey },
                                label = {
                                    Text(
                                        text = if (filterKey == "الكل") "جميع السور (١١٤)" else label,
                                        fontSize = 11.5.sp,
                                        fontWeight = if (selectedFilter == filterKey) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldPrimary,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.testTag("filter_chip_$filterKey")
                            )
                        }
                    }

                    // Section / Juz Quick Filters
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            Text(
                                text = "الأجزاء:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(end = 4.dp, top = 8.dp)
                            )
                        }
                        val juzOptions = listOf(
                            "جميع الأجزاء",
                            "جزء عمّ (30)",
                            "جزء تبارك (29)",
                            "جزء قد سمع (28)",
                            "السبع الطوال (1-9)"
                        )
                        items(juzOptions) { option ->
                            val isSelected = selectedJuzFilter == option
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) GoldPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                onClick = { selectedJuzFilter = option },
                                modifier = Modifier.testTag("juz_filter_${option.take(6)}")
                            ) {
                                Text(
                                    text = option,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Results count header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "قائمة السور المتاحة (${toArabicDigits(filteredSurahs.size)} سورة):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "اضغط على السورة للقراءة 📖",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = EmeraldPrimary
                    )
                }
            }

            // Surahs LazyColumn items
            if (filteredSurahs.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "لم يتم العثور على سورة مطابقة",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "يرجى تجربة البحث باسم آخر مثل «الفاتحة»، «البقرة»، «الكهف»، «يس» أو كتابة رقم السورة",
                                fontSize = 12.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(filteredSurahs, key = { it.id }) { surah ->
                    val isCurrentlySelected = surah.id == selectedSurahId

                    SurahIndexCard(
                        surah = surah,
                        isSelected = isCurrentlySelected,
                        onOpenPlayer = { onSelectSurah(surah) },
                        onOpenPractice = { onNavigateToPractice(surah) },
                        onToggleFavorite = { onToggleFavorite(surah.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatPill(
    title: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = title,
            fontSize = 10.5.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SurahIndexCard(
    surah: Surah,
    isSelected: Boolean,
    onOpenPlayer: () -> Unit,
    onOpenPractice: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) EmeraldPrimary.copy(alpha = 0.06f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onOpenPlayer() } // Clicking anywhere on the Surah card navigates to the reading screen!
            .testTag("surah_card_${surah.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Main Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Surah Number Badge with Islamic Circular Gold Ornament
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) EmeraldPrimary else EmeraldPrimary.copy(alpha = 0.12f)
                            )
                            .border(1.5.dp, GoldPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${surah.number}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else EmeraldPrimary
                            )
                            Text(
                                text = toArabicDigits(surah.number),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isSelected) GoldLight else GoldDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "سُورَةُ ${surah.nameArabic}",
                                fontSize = 17.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmeraldPrimary
                                ) {
                                    Text(
                                        text = "المحددة ✓",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        // Subtitle: English Transliteration & Details
                        Text(
                            text = "${surah.nameEnglish} • ${surah.versesCount} آيات (${toArabicDigits(surah.versesCount)})",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Trailing Action: Bookmark & Navigation Indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_favorite_surah_${surah.id}")
                    ) {
                        Icon(
                            imageVector = if (isSelected) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "حفظ السورة",
                            tint = if (isSelected) GoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Forward arrow indicating clickability
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "انتقال لشاشة القراءة",
                        tint = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metadata Badges Row (Revelation Type, Juz, Educational feature)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Revelation Type Badge
                val isMeccan = surah.revelationType.contains("مكية")
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isMeccan) EmeraldPrimary.copy(alpha = 0.12f) else Color(0xFF0277BD).copy(alpha = 0.12f)
                ) {
                    Text(
                        text = if (isMeccan) "مكية 🕋" else "مدنية 🕌",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isMeccan) EmeraldPrimary else Color(0xFF0277BD),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                // Juz Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GoldPrimary.copy(alpha = 0.14f)
                ) {
                    Text(
                        text = getSurahJuzName(surah.number),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GoldDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                // Verses count pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = "${surah.versesCount} آية",
                        fontSize = 10.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Educational / Tafsir Notes snippet if available
            if (surah.educationalNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                ) {
                    Text(
                        text = surah.educationalNotes,
                        fontSize = 11.5.sp,
                        lineHeight = 16.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            // Bottom Action Buttons
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Primary Action Button: Read Surah (navigates to reading screen)
                Button(
                    onClick = onOpenPlayer,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldPrimary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_read_surah_${surah.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "قراءة السورة الآن 📖",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Secondary Action Button: Practice / Audio
                OutlinedButton(
                    onClick = onOpenPractice,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = EmeraldPrimary
                    ),
                    border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.6f)),
                    modifier = Modifier.testTag("btn_open_practice_surah_${surah.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "تدريب",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
