package com.example.raspapp

import android.content.Context
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceComposable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.Action
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.color.ColorProviders
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.raspapp.data.RaspItem
import com.example.raspapp.ui.theme.RaspAppTheme
import com.example.raspapp.viewmodel.ScheduleViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CurrentLessonGlanceWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Single

    @Composable
    @GlanceComposable
    fun Content() {
        val viewModel: ScheduleViewModel = viewModel() // Используем ViewModel
        val currentLesson = getCurrentLesson(viewModel)

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(8.dp)
                .background(MaterialTheme.colors.background) // Фон как в вашем дизайне
                .clickable(onClick = openAppAction()), // Клик открывает приложение
            verticalAlignment = Alignment.CenterVertically
        ) {1
            currentLesson?.let { lesson ->
                val backgroundColor = when (lesson.discipline.substring(0, 3)) {
                    "лек" -> androidx.glance.color.Color("#1E9D99")
                    "пр " -> androidx.glance.color.Color("#FF9D58")
                    "лаб" -> androidx.glance.color.Color("#8F2F64")
                    "ЗчО" -> androidx.glance.color.Color("#5447C9")
                    "Кон" -> androidx.glance.color.Color("#5CCCCC")
                    "Экз" -> androidx.glance.color.Color("#FD4949")
                    else -> androidx.glance.color.Color("#8D8D8D")
                }

                Column(
                    modifier = glanceModifier
                        .background(backgroundColor)
                        .padding(4.dp)
                ) {
                    Text(
                        text = "${lesson.lessonNumber}-е занятие",
                        style = TextStyle(
                            color = androidx.glance.color.Color.White,
                            fontSize = 12.sp
                        )
                    )
                    Row(
                        modifier = glanceModifier.padding(4.dp)
                    ) {
                        Text(
                            text = when (lesson.discipline.substring(0, 3)) {
                                "пр " -> lesson.discipline.substring(3)
                                "Кон" -> lesson.discipline.substring(5)
                                else -> lesson.discipline.substring(4)
                            },
                            style = TextStyle(
                                color = androidx.glance.color.Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = glanceModifier.weight(2f)
                        )
                        Text(
                            text = "${lesson.startTime} - ${lesson.endTime}",
                            style = TextStyle(
                                color = androidx.glance.color.Color.White,
                                fontSize = 12.sp
                            ),
                            modifier = glanceModifier.weight(1f)
                        )
                    }
                    Text(
                        text = when (lesson.discipline.substring(0, 3)) {
                            "лек" -> "Лекция"
                            "пр " -> "Практика"
                            "лаб" -> "Лабораторная"
                            "ЗчО" -> "Зачёт"
                            "Кон" -> "Консультация"
                            "Экз" -> "Экзамен"
                            else -> "Неизвестно"
                        },
                        style = TextStyle(
                            color = androidx.glance.color.Color.White,
                            fontSize = 12.sp
                        )
                    )
                    Row(
                        modifier = glanceModifier.padding(4.dp)
                    ) {
                        Text(
                            text = "ауд. ${lesson.auditorium.drop(3)} корп. ${lesson.auditorium[1]}",
                            style = TextStyle(
                                color = androidx.glance.color.Color.White,
                                fontSize = 12.sp
                            ),
                            modifier = glanceModifier.weight(0.4f)
                        )
                        Text(
                            text = lesson.teacher,
                            style = TextStyle(
                                color = androidx.glance.color.Color.White,
                                fontSize = 12.sp
                            ),
                            modifier = glanceModifier.weight(0.6f)
                        )
                    }
                }
            } ?: Text(
                text = "Нет текущей пары",
                style = TextStyle(
                    color = androidx.glance.color.Color.White,
                    fontSize = 16.sp
                )
            )
        }
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Content()
        }
    }

    // Действие для открытия приложения
    private fun openAppAction() = object : ActionRunnable {
        override suspend fun run(context: Context, parameters: ActionParameters) {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
}

class CurrentLessonWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = CurrentLessonGlanceWidget()
}

// Функция для получения текущей пары
fun getCurrentLesson(viewModel: ScheduleViewModel): RaspItem? {
    val schedule = viewModel.schedule.value?.data?.rasp ?: emptyList()
    val now = LocalDateTime.now() // 05:19 PM CEST, 13.06.2025
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

    return schedule.find { item ->
        val start = LocalDateTime.parse(item.startDate, formatter)
        val end = LocalDateTime.parse(item.endDate, formatter)
        val dinnerBreak = when {
            item.auditorium[1] == '2' -> "конец"
            item.auditorium[3].digitToInt() > 2 -> "середина"
            else -> "начало"
        }
        val dinnerRange = getDinnerDateTime(item, dinnerBreak)
        now in start..end && now !in dinnerRange
    }
}