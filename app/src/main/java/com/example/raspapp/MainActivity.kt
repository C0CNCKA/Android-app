package com.example.raspapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face6
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.raspapp.data.RaspItem
import com.example.raspapp.ui.theme.RaspAppTheme
import com.example.raspapp.viewmodel.ScheduleViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RaspAppTheme {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                )
                {
                    ScheduleScreen(isSystemInDarkTheme())
                }
            }
        }
    }
}

@Composable
fun ScheduleScreen(isDarkTheme: Boolean, viewModel: ScheduleViewModel = viewModel()) {
    val schedule by viewModel.schedule.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current
    var offline by remember { mutableStateOf(false) }
    var actualDateTime by remember { mutableStateOf(LocalDateTime.now()) }
    var currentWeekDisplay: Long by remember { mutableStateOf(0) }
    var viewModel: ScheduleViewModel = viewModel()
    val weekFormatter = DateTimeFormatter.ofPattern("dd.MM.yy")
    val dayOfWeek = actualDateTime.dayOfWeek
    val startOfWeek = actualDateTime.minusDays((dayOfWeek.value - DayOfWeek.MONDAY.value).toLong())
    val endOfWeek = startOfWeek.plusDays(6)
    LaunchedEffect(Unit) {
        viewModel.loadSchedule(context, LocalDateTime.now())
    }

    Log.d("MainActivity", "Состояние: isLoading=$isLoading, error=$error, schedule=$schedule")

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        when {
            isLoading -> {
                Card(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 350.dp, start = 25.dp, end = 25.dp)
                ) {
                    Text(
                        "Загрузка расписания...",
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = {
                            offline = true
                            viewModel.loadOfflineSchedule(context)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                    ) {
                        Text(textAlign = TextAlign.Center,
                            text = "Открыть офлайн-версию"
                        )
                    }
                }

            }

            error != null -> {
                Text(
                    text = error ?: "Неизвестная ошибка",
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(onClick = { viewModel.loadSchedule(context, LocalDateTime.now()) }) {
                    Text("Попробовать снова")
                }
                Button(
                    onClick = {
                        offline = true
                        viewModel.loadOfflineSchedule(context)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text("Открыть офлайн-версию")
                }
            }

            schedule != null && schedule!!.data.rasp.isNotEmpty() -> {
                val raspItems = schedule!!.data.rasp
//                val bringIntoViewRequesters = remember {
//                    sections.associateWith { BringIntoViewRequester() }
//                rememberCoroutineScope().launch {
//                    bringIntoViewRequesters[section]?.bringIntoView()
//                }
                Column {
                    if (offline) {
                        Card(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                        ) {
                            Text(
                                "Загружено оффлайн расписание",
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    val grouped = raspItems.groupBy { it.dayOfWeekName }
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .weight(1f)
                    ) {
                        grouped.forEach { (_, itemsForDay) ->
                            DayOfWeekCard(itemsForDay[1])
                            Card(
                                shape = RoundedCornerShape(bottomStart = 16.dp),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 4.dp
                                ),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.background
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 8.dp, bottom = 8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    itemsForDay.forEach { item ->
                                        EtoKartochkaEbat(item)
                                    }
                                }
                            }
                        }
                    }
//                        //ПоказыватьНеделю!!!!
                    Card(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .background(MaterialTheme.colorScheme.background)
                            .padding(horizontal = 8.dp, vertical = 16.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(190.dp),
                        colors = CardDefaults.cardColors(
                            MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.2f
                            )
                        )
                    )
                    {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .align(Alignment.CenterHorizontally)
                                .fillMaxWidth()
                        ) {
                            Button(
                                modifier = Modifier.padding(vertical = 0.dp),
                                onClick = {
                                    Log.d("неделя", currentWeekDisplay.toString())
                                    currentWeekDisplay -= 1
                                    viewModel.loadSchedule(
                                        context,
                                        actualDateTime.plusWeeks(currentWeekDisplay)
                                    )
                                },

                                ) {
                                Text("<<", fontSize = 20.sp)
                            }
                            Text(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 0.dp),
                                text = startOfWeek.format(weekFormatter) + "-" + endOfWeek.format(
                                    weekFormatter
                                ),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Button(
                                modifier = Modifier.padding(vertical = 0.dp),
                                onClick = {
                                    Log.d("неделя", currentWeekDisplay.toString())
                                    currentWeekDisplay += 1
                                    viewModel.loadSchedule(
                                        context,
                                        actualDateTime.plusWeeks(currentWeekDisplay)
                                    )

                                },
                            ) {
                                Text(">>", fontSize = 20.sp)
                            }
                        }
                    }
                }
            }
            else -> { //Показывать неделю хз откуда!!!
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f))
                {
                    Card(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(top = 350.dp, start = 25.dp, end = 25.dp, bottom = 350.dp)
                    ) {
                        Text(
                            "Нет занятий на выбранную неделю",
                            modifier = Modifier.padding(12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Card(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 8.dp, vertical = 16.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(190.dp),
                    colors = CardDefaults.cardColors(
                        MaterialTheme.colorScheme.primary.copy(
                            alpha = 0.2f
                        )
                    )
                )
                {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth()
                    ) {
                        Button(
                            modifier = Modifier.padding(vertical = 0.dp),
                            onClick = {
                                Log.d("неделя", currentWeekDisplay.toString())
                                currentWeekDisplay -= 1
                                viewModel.loadSchedule(
                                    context,
                                    actualDateTime.plusWeeks(currentWeekDisplay)
                                )
                            },

                            ) {
                            Text("<<", fontSize = 20.sp)
                        }
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 0.dp),
                            text = startOfWeek.format(weekFormatter) + "-" + endOfWeek.format(
                                weekFormatter
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Button(
                            modifier = Modifier.padding(vertical = 0.dp),
                            onClick = {
                                Log.d("неделя", currentWeekDisplay.toString())
                                currentWeekDisplay += 1
                                viewModel.loadSchedule(
                                    context,
                                    actualDateTime.plusWeeks(currentWeekDisplay)
                                )

                            },
                        ) {
                            Text(">>", fontSize = 20.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DayOfWeekCard(item: RaspItem) {
    val formatter = DateTimeFormatter.ofPattern("d MMMM", Locale("ru"))
    val localDate = LocalDate.parse(item.date.substring(0, 10))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
    ) {
        Text(
            "${item.dayOfWeekName} - ${localDate.format(formatter)}",
            modifier = Modifier
                .padding(start = 24.dp, bottom = 16.dp, top = 16.dp)
                .wrapContentWidth(),
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}

fun getDinnerDateTime(item: RaspItem, dinnerBreak: String): ClosedRange<LocalDateTime> {
    val lessonDateString = item.date.substring(0, 11)
    return when (dinnerBreak) {
        "конец" ->
            LocalDateTime.parse("${lessonDateString}13:20:00")..LocalDateTime.parse("${lessonDateString}13:50:00")

        "середина" ->
            LocalDateTime.parse("${lessonDateString}12:30:00")..LocalDateTime.parse("${lessonDateString}12:50:00")

        else ->
            LocalDateTime.parse("${lessonDateString}11:40:00")..LocalDateTime.parse("${lessonDateString}12:10:00")
    }
}

@Composable
fun DinnerCard(item: RaspItem, dinnerBreak: String) {
//    var actualDateTime by remember { mutableStateOf(LocalDateTime.parse("2025-05-28T12:35:00"))}
    var actualDateTime by remember { mutableStateOf(LocalDateTime.now()) }
    val dinnerBreakDateTime = getDinnerDateTime(item, dinnerBreak)
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val backgroundColor = Color(0xFFFF4A53)
    var isEnded by remember { mutableStateOf(actualDateTime > dinnerBreakDateTime.endInclusive) }
    var isNow by remember { mutableStateOf(actualDateTime in dinnerBreakDateTime) }
    LaunchedEffect(Unit) {
        while (true) {
            actualDateTime = LocalDateTime.now()
            isEnded = actualDateTime > dinnerBreakDateTime.endInclusive
            isNow = actualDateTime in dinnerBreakDateTime
            kotlinx.coroutines.delay(60000)
        }
    }

    Box(
        modifier = Modifier.graphicsLayer(alpha = if (isEnded) 0.8f else 1f)
    ) {
        Card(
            modifier = if (isNow) {
                Modifier
                    .blur(15.dp, BlurredEdgeTreatment.Unbounded)
                    .graphicsLayer(alpha = 0.8f)
            } else {
                Modifier
                    .blur(15.dp, BlurredEdgeTreatment.Unbounded)
                    .graphicsLayer(alpha = 0.3f)
            }
                .matchParentSize()
                .zIndex(0f),
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor
            )
        ) {}
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor
            )
        ) {
            Card(
                shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp),
                modifier = Modifier
                    .padding(start = 8.dp, top = 2.dp)
                    .zIndex(2f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = if (!isNow) 0.975f else 0.8f)
                )
            ) {
                Column(
                    modifier = if (isNow) {
                        Modifier
                    } else {
                        Modifier
                    }.padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Row(modifier = Modifier.weight(2f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start)
                        {
                            Text(
                                text = "Обед - $dinnerBreak пары",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                            )
                            if (isNow) {
                                Icon(
                                    modifier = Modifier
                                        .padding(start = 3.dp)
                                        .size(20.dp)
                                        .offset(x = -1.dp),
                                    imageVector = Icons.Filled.Schedule,
                                    contentDescription = "",
                                    tint = backgroundColor.copy(alpha = 0.9f),
                                )
                            }
                        }
                        Text(
                            "${dinnerBreakDateTime.start.format(formatter)} - ${
                                dinnerBreakDateTime.endInclusive.format(
                                    formatter
                                )
                            }",
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .padding(top = 1.dp)
                                .weight(1f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EtoKartochkaEbat(item: RaspItem) {
    var expanded by remember { mutableStateOf(false) }
    //var actualDateTime = LocalDateTime.parse("2025-05-28T13:35:00")
    var actualDateTime by remember { mutableStateOf(LocalDateTime.now()) }
    val lessonType = item.discipline.substring(0, 3)
    val lesson = item.discipline
    var isEnded by remember { mutableStateOf(actualDateTime > LocalDateTime.parse(item.endDate)) }
    val dinnerBreak = when {
        item.auditorium[1] == '2' ->
            "конец"
        item.auditorium[3].digitToInt() > 2 ->
            "середина"
        else ->
            "начало"
    }
    val dinnerBreakDateTime = getDinnerDateTime(item, dinnerBreak)
    var isNow by remember {
        mutableStateOf(
            (actualDateTime in LocalDateTime.parse(item.startDate)..LocalDateTime.parse(
                item.endDate
            )) && (actualDateTime !in dinnerBreakDateTime)
        )
    }
    var backgroundColor = try {
        when (lessonType) {
            "лек" -> Color(0xFF1E9D99)
            "пр " -> Color(0xFFFF9D58)
            "лаб" -> Color(0xFF8F2F64)
            "ЗчО" -> Color(0xFF5447C9)
            "Кон" -> Color(0xFF5CCCCC)
            "Экз" -> Color(0xFFFD4949)
            else -> Color(0xFF8D8D8D)
        }
    } catch (e: Exception) {
        Color(0xFF8D8D8D)
    }

    LaunchedEffect(Unit) {
        while (true) {
            actualDateTime = LocalDateTime.now()
            isEnded = actualDateTime > LocalDateTime.parse(item.endDate)
            isNow =
                (actualDateTime in LocalDateTime.parse(item.startDate)..LocalDateTime.parse(item.endDate)) && (actualDateTime !in dinnerBreakDateTime)
            kotlinx.coroutines.delay(60000)
        }
    }
    Column(
        modifier = Modifier
            .padding(start = 8.dp, end = 16.dp)
            .graphicsLayer(alpha = if (isEnded) 0.3f else 1f)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }) {
                expanded = !expanded
            }) {
        Text(
            "${item.lessonNumber}-е занятие", fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
        Box(
            modifier = Modifier
        ) {
            Card(
                modifier = if (isNow) {
                    Modifier
                        .blur(35.dp, BlurredEdgeTreatment.Unbounded)
                        .graphicsLayer(alpha = 0.8f)
                } else {
                    Modifier
                        .blur(25.dp, BlurredEdgeTreatment.Unbounded)
                        .graphicsLayer(alpha = 0.3f)
                }
                    .matchParentSize()
                    .zIndex(0f),
                colors = CardDefaults.cardColors(
                    containerColor = backgroundColor
                )
            ) {}
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = backgroundColor
                )
            ) {
                Card(
                    shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp),
                    modifier = Modifier
                        .padding(start = 8.dp, top = 2.dp)
                        .zIndex(2f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = if (!isNow) 0.975f else 0.8f)
                    )
                ) {
                    Box {
                        //Тут должен быть треугольник
                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                        ) {
                            if (item.lessonNumber == 3 && dinnerBreak == "начало") {
                                DinnerCard(item, dinnerBreak)
                            }
                            Row (modifier = Modifier
                                .padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween)
                            {
                                Row(modifier = Modifier.weight(2f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Text(
                                        text = if (lessonType == "пр ") lesson.substring(3)
                                        else if (lessonType == "Кон") lesson.substring(5)
                                        else lesson.substring(4),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                    )
                                    if (isNow) {
                                        Icon(
                                            modifier = Modifier
                                                .padding(start = 3.dp)
                                                .size(20.dp)
                                                .offset(x = -1.dp),
                                            imageVector = Icons.Filled.Schedule,
                                            contentDescription = "",
                                            tint = backgroundColor.copy(alpha = 0.9f)
                                        )
                                    }
                                }
                                    Text(
                                        "${item.startTime} - ${item.endTime}",
                                        textAlign = TextAlign.End,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(top = 1.dp),
                                        fontSize = 12.sp,
                                    )
                            }
                            Spacer(Modifier.padding(vertical = 1.dp))
                            Text(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                text = when (lessonType) {
                                    "лек" -> "Лекция"
                                    "пр " -> "Практика"
                                    "лаб" -> "Лабораторная"
                                    "ЗчО" -> "Зачёт"
                                    "Кон" -> "Консультация"
                                    "Экз" -> "Экзамен"
                                    else -> "абеба"
                                },
                                fontSize = 12.sp
                            )
                            if (item.lessonNumber == 3 && dinnerBreak == "середина") {
                                Box(modifier = Modifier.padding(vertical = 4.dp)) {
                                    DinnerCard(item, dinnerBreak)
                                }
                            } else {
                                Spacer(Modifier.padding(vertical = 5.dp))
                            }
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Text(modifier = Modifier.weight(0.4f),
                                    text = "ауд. ${item.auditorium.drop(3)} корп. ${item.auditorium[1]}",
                                    fontSize = 12.sp
                                )
                                Row (modifier = Modifier.weight(0.6f)){
                                    if (item.replacement) {
                                        Icon(
                                            modifier = Modifier
                                                .padding(start = 3.dp)
                                                .size(20.dp)
                                                .offset(x = -1.dp, y = 2.dp),
                                            imageVector = Icons.Default.Face6,
                                            contentDescription = "",
                                            tint = backgroundColor.copy(alpha = 0.9f)
                                        )
                                    }
                                    Text(
                                        text = if (!expanded) item.teacher
                                        else item.teacherFullName,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier
                                            .padding(bottom = 4.dp),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            if (item.lessonNumber == 3 && dinnerBreak == "конец") {
                                DinnerCard(item, dinnerBreak)
                            }
                        }
                    }
                }
            }
        }
    }
}
