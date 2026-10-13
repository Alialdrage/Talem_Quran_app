package com.example.ui.components

import android.Manifest
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reminder.DailyReminderManager
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldPrimary

@Composable
fun DailyQuranReminderCard(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var isEnabled by remember {
        mutableStateOf(DailyReminderManager.isReminderEnabled(context))
    }

    var reminderTime by remember {
        mutableStateOf(DailyReminderManager.getReminderTime(context))
    }

    var showTestSentSuccess by remember { mutableStateOf(false) }

    // Launcher for Android 13+ POST_NOTIFICATIONS permission
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            DailyReminderManager.setReminderEnabled(context, true)
            isEnabled = true
            DailyReminderManager.sendImmediateTestNotification(context)
            showTestSentSuccess = true
            Toast.makeText(context, "تم تفعيل إشعارات تذكير الورد القرآني بنجاح", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "يرجى السماح بالإشعارات لتلقي التذكير اليومي", Toast.LENGTH_LONG).show()
        }
    }

    val presetTimes = listOf(
        Triple(5, 30, "05:30 ص (الفجر)"),
        Triple(8, 30, "08:30 ص (الصباح)"),
        Triple(16, 30, "04:30 م (العصر)"),
        Triple(20, 30, "08:30 م (المساء)")
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = GoldPrimary.copy(alpha = 0.08f)
        ),
        border = BorderStroke(1.2.dp, GoldPrimary.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_quran_reminder_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Bell Icon, Title, and Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isEnabled) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                            contentDescription = "التذكير اليومي",
                            tint = if (isEnabled) GoldDark else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "التذكير اليومي بالورد القرآني 🔔",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark
                        )
                        Text(
                            text = "تنبيه يومي مبارك لتعاهد وتدبر كتاب الله",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Switch(
                    checked = isEnabled,
                    onCheckedChange = { checked ->
                        if (checked) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                DailyReminderManager.setReminderEnabled(context, true)
                                isEnabled = true
                            }
                        } else {
                            DailyReminderManager.setReminderEnabled(context, false)
                            isEnabled = false
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = EmeraldPrimary,
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.testTag("switch_daily_reminder")
                )
            }

            AnimatedVisibility(
                visible = isEnabled,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                    // Time selector label
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "اختر الوقت الأنسب لوردك اليومي:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Preset Times Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presetTimes.forEach { (h, m, label) ->
                            val isSelected = reminderTime.first == h && reminderTime.second == m
                            Surface(
                                onClick = {
                                    reminderTime = Pair(h, m)
                                    DailyReminderManager.setReminderTime(context, h, m)
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) EmeraldPrimary else Color.White,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) EmeraldDark else EmeraldPrimary.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("chip_reminder_time_${h}_$m")
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Reminder preview banner
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "⏰ موعد التنبيه القادم: يومياً في الساعة ${String.format("%02d:%02d", reminderTime.first, reminderTime.second)}",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Test Notification Button
                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                            DailyReminderManager.sendImmediateTestNotification(context)
                            showTestSentSuccess = true
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_test_notification")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "تجربة التنبيه",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "تجربة إرسال التنبيه الآن إلى هاتفي 🔔",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    AnimatedVisibility(visible = showTestSentSuccess) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldPrimary.copy(alpha = 0.12f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "تم إرسال إشعار التذكير بنجاح، تفقّد شريط الإشعارات العلوي ✨",
                                    fontSize = 11.sp,
                                    color = EmeraldDark,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
