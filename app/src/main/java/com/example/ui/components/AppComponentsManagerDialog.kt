package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.data.model.Surah
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldPrimary

/**
 * زر تفاعلي مخصص للتحميل والحذف لسورة محددة
 * يُعرض في شاشة المصحف التفاعلي وبطاقات فهرس السور
 */
@Composable
fun SurahDownloadDeleteButton(
    surah: Surah,
    isDownloaded: Boolean,
    isDownloading: Boolean,
    downloadProgress: Float,
    onDownload: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showConfirmDelete by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
    ) {
        when {
            isDownloading -> {
                // حالة جاري التحميل
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = EmeraldPrimary.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.4f)),
                    modifier = Modifier.testTag("downloading_indicator_${surah.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { downloadProgress },
                            modifier = Modifier.size(14.dp),
                            color = EmeraldPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "جاري التحميل ${(downloadProgress * 100).toInt()}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                    }
                }
            }

            isDownloaded -> {
                // شارة تم التحميل + زر الحذف
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldPrimary.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.35f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "محمل",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "بدون إنترنت ✓",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                    }
                }

                // زر الحذف
                Surface(
                    onClick = { showConfirmDelete = true },
                    shape = RoundedCornerShape(8.dp),
                    color = ErrorRed.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, ErrorRed.copy(alpha = 0.4f)),
                    modifier = Modifier.testTag("btn_delete_surah_${surah.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "حذف الملف",
                            tint = ErrorRed,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "حذف",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = ErrorRed
                        )
                    }
                }
            }

            else -> {
                // زر التحميل
                Surface(
                    onClick = onDownload,
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldPrimary.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.4f)),
                    modifier = Modifier.testTag("btn_download_surah_${surah.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "تحميل السورة",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "تحميل 📥",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                    }
                }
            }
        }
    }

    // نافذة تأكيد حذف الملف
    if (showConfirmDelete) {
        AlertDialog(
            onDismissRequest = { showConfirmDelete = false },
            title = {
                Text(
                    text = "حذف تلاوة ${surah.nameArabic}؟",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "هل ترغب في حذف الملف الصوتي المحمّل لسورة ${surah.nameArabic} لتوفير مساحة التخزين؟ يمكنك إعادة تحميله في أي وقت.",
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDelete = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("حذف الملف 🗑️", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDelete = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

/**
 * نافذة «إدارة مكونات التطبيق والتخزين» الشاملة:
 * توفر للمستخدم أزرار التحميل والحذف لكافة مكونات التطبيق:
 * 1. التلاوات الصوتية للسور (تحميل/حذف السورة الحالية، تحميل الكل، حذف الكل)
 * 2. تسجيلات تدريب التلاوة الصوتية (عرض الحجم، حذف التسجيلات)
 * 3. بيانات الإنجاز (تصدير/تنزيل نسخة احتياطية، حذف وتصفير الإنجاز)
 */
@Composable
fun AppComponentsManagerDialog(
    currentSurah: Surah,
    isCurrentSurahDownloaded: Boolean,
    isCurrentSurahDownloading: Boolean,
    downloadProgress: Float,
    downloadedSurahsCount: Int,
    downloadedAudioSizeFormatted: String,
    recordingsSizeFormatted: String,
    onDownloadCurrentSurah: () -> Unit,
    onDeleteCurrentSurah: () -> Unit,
    onDownloadAllSurahs: () -> Unit,
    onDeleteAllSurahs: () -> Unit,
    onDeleteAllRecordings: () -> Unit,
    onExportProgressBackup: () -> Unit,
    onResetProgress: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var showConfirmDeleteAllSurahs by remember { mutableStateOf(false) }
    var showConfirmDeleteRecordings by remember { mutableStateOf(false) }
    var showConfirmResetProgress by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 20.dp)
                .testTag("app_components_manager_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SdStorage,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "إدارة مكونات التطبيق والتخزين",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "أزرار التحميل والحذف للملفات والتسجيلات والبيانات",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp).testTag("btn_close_components_manager")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // بطاقة ملخص السعة التخزينية
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldPrimary.copy(alpha = 0.08f)),
                    border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = downloadedAudioSizeFormatted,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                            Text(
                                text = "تلاوات محملة ($downloadedSurahsCount سورة)",
                                fontSize = 10.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(28.dp)
                                .background(EmeraldPrimary.copy(alpha = 0.3f))
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = recordingsSizeFormatted,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldDark
                            )
                            Text(
                                text = "تسجيلات تدريب صوتية",
                                fontSize = 10.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ===============================================
                // القسم الأول: التلاوات الصوتية للسور (تحميل / حذف)
                // ===============================================
                Text(
                    text = "١. التلاوات الصوتية للسور (للاستماع بدون إنترنت):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldDark
                )
                Spacer(modifier = Modifier.height(8.dp))

                // بطاقة السورة الحالية مع أزرار التحميل والحذف المباشرة
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = currentSurah.nameArabic,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isCurrentSurahDownloaded) "الملف متاح محلياً ومُحمّل ✓" else "غير محملة محلياً",
                                fontSize = 11.sp,
                                color = if (isCurrentSurahDownloaded) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        SurahDownloadDeleteButton(
                            surah = currentSurah,
                            isDownloaded = isCurrentSurahDownloaded,
                            isDownloading = isCurrentSurahDownloading,
                            downloadProgress = downloadProgress,
                            onDownload = onDownloadCurrentSurah,
                            onDelete = onDeleteCurrentSurah
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // أزرار التحميل الشامل والحذف الشامل للسور
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // زر تحميل جميع السور
                    Button(
                        onClick = onDownloadAllSurahs,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier.weight(1f).testTag("btn_download_all_surahs")
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تحميل السور 📥", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // زر حذف جميع السور المحملة
                    OutlinedButton(
                        onClick = { showConfirmDeleteAllSurahs = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                        border = BorderStroke(1.dp, ErrorRed.copy(alpha = 0.5f)),
                        modifier = Modifier.weight(1f).testTag("btn_delete_all_surahs")
                    ) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("حذف المحمل 🗑️", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(thickness = 0.8.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))

                // ===============================================
                // القسم الثاني: تسجيلات تدريب التلاوة الشخصية (حذف)
                // ===============================================
                Text(
                    text = "٢. تسجيلات تدريب التلاوة الصوتية الشخصية:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldDark
                )
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "التسجيلات التي سجلتها أثناء تدريب مخارج الحروف والتلاوة في قسم «تدريب التلاوة».",
                    fontSize = 11.5.sp,
                    lineHeight = 17.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                // زر حذف تسجيلات التدريب
                OutlinedButton(
                    onClick = { showConfirmDeleteRecordings = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                    border = BorderStroke(1.dp, ErrorRed.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().testTag("btn_delete_all_recordings")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "حذف وتفريغ جميع التسجيلات الصوتية ($recordingsSizeFormatted) 🗑️",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(thickness = 0.8.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))

                // ===============================================
                // القسم الثالث: بيانات وسجل الإنجاز (تنزيل / حذف)
                // ===============================================
                Text(
                    text = "٣. بيانات وسجل الإنجاز والمفضلة والفواصل:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // زر تنزيل / تصدير نسخة احتياطية من الإنجاز
                    Button(
                        onClick = onExportProgressBackup,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldDark),
                        modifier = Modifier.weight(1.1f).testTag("btn_export_progress_backup")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تنزيل نسخة الإنجاز 💾", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // زر تصفير وحذف سجل الإنجاز
                    OutlinedButton(
                        onClick = { showConfirmResetProgress = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                        border = BorderStroke(1.dp, ErrorRed.copy(alpha = 0.5f)),
                        modifier = Modifier.weight(0.9f).testTag("btn_reset_progress_data")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تصفير الإنجاز 🗑️", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // تأكيد حذف جميع السور المحملة
    if (showConfirmDeleteAllSurahs) {
        AlertDialog(
            onDismissRequest = { showConfirmDeleteAllSurahs = false },
            title = {
                Text("حذف جميع التلاوات المحملة؟", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("سيتم حذف جميع الملفات الصوتية المحملة لسور القرآن وتفريغ مساحة الذاكرة ($downloadedAudioSizeFormatted). يمكنك إعادة تحميل أي سورة لاحقاً.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDeleteAllSurahs = false
                        onDeleteAllSurahs()
                        Toast.makeText(context, "تم حذف التلاوات المحملة وتفريغ المساحة", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("نعم، حذف الكل 🗑️", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDeleteAllSurahs = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // تأكيد حذف التسجيلات الصوتية
    if (showConfirmDeleteRecordings) {
        AlertDialog(
            onDismissRequest = { showConfirmDeleteRecordings = false },
            title = {
                Text("حذف جميع التسجيلات الصوتية؟", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("سيتم حذف جميع ملفات التسجيل الصوتي الشخصية ($recordingsSizeFormatted) بشكل نهائي.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDeleteRecordings = false
                        onDeleteAllRecordings()
                        Toast.makeText(context, "تم حذف التسجيلات الصوتية بنجاح", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("حذف التسجيلات 🗑️", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDeleteRecordings = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // تأكيد تصفير الإنجاز
    if (showConfirmResetProgress) {
        AlertDialog(
            onDismissRequest = { showConfirmResetProgress = false },
            title = {
                Text("إعادة ضبط وسجل الإنجاز؟", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("هل ترغب في إعادة ضبط الدروس المكتملة وعدد التسجيلات إلى البداية؟")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmResetProgress = false
                        onResetProgress()
                        Toast.makeText(context, "تمت إعادة ضبط سجل الإنجاز", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("إعادة ضبط 🗑️", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmResetProgress = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
