package com.example.raspapp.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.raspapp.api.RetrofitInstance
import com.example.raspapp.data.ScheduleResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.Date
import java.util.Locale
import java.io.File
import java.time.format.DateTimeFormatter


class ScheduleViewModel : ViewModel() {
    private val _schedule = MutableStateFlow<ScheduleResponse?>(null)
    val schedule: StateFlow<ScheduleResponse?> = _schedule

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadSchedule(context: Context, date: LocalDateTime) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val currentDate = date.format(formatter)
                Log.d("ScheduleViewModel", "Запрос к API с датой: $currentDate")
                val response = RetrofitInstance.api.getSchedule(idGroup = 29202, sdate = currentDate)
                val json = Gson().toJson(response)
                File(context.filesDir, "offline_schedule.json").writeText(json)
                Log.d("ScheduleViewModel", "Получен ответ: $response")
                _schedule.value = response
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки: ${e.message}"
                Log.e("ScheduleViewModel", "Ошибка: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadOfflineSchedule(context: Context) {
        val file = File(context.filesDir, "offline_schedule.json")
        if (file.exists()) {
            val offlineJson = file.readText()
            val offlineSchedule = Gson().fromJson(offlineJson, ScheduleResponse::class.java)
            _schedule.value = offlineSchedule
            _error.value = null
            _isLoading.value = false
        }
    }
}