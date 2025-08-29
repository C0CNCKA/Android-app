package com.example.raspapp.data

import com.google.gson.annotations.SerializedName

data class ScheduleResponse(
    @SerializedName("data")
    val data: ScheduleData
)

data class ScheduleData(
    @SerializedName("isCyclicalSchedule")
    val isCyclicalSchedule: Boolean,
    @SerializedName("rasp")
    val rasp: List<RaspItem>
)

data class RaspItem(
    @SerializedName("код")
    val code: Int,
    @SerializedName("дата")
    val date: String,
    @SerializedName("датаНачала")
    val startDate: String,
    @SerializedName("датаОкончания")
    val endDate: String? = null,
    @SerializedName("начало")
    val startTime: String,
    @SerializedName("конец")
    val endTime: String,
    @SerializedName("деньНедели")
    val dayOfWeek: Int,
    @SerializedName("день_недели")
    val dayOfWeekName: String,
    @SerializedName("типНедели")
    val weekType: Int,
    @SerializedName("дисциплина")
    val discipline: String,
    @SerializedName("преподаватель")
    val teacherFullName: String,
    @SerializedName("должность")
    val position: String,
    @SerializedName("аудитория")
    val auditorium: String,
    @SerializedName("часы")
    val time: String,
    @SerializedName("неделяНачала")
    val startWeek: Int,
    @SerializedName("неделяОкончания")
    val endWeek: Int,
    @SerializedName("замена")
    val replacement: Boolean,
    @SerializedName("кодГруппы")
    val groupCode: Int,
    @SerializedName("фиоПреподавателя")
    val teacher: String,
    @SerializedName("номерЗанятия")
    val lessonNumber: Int,
    @SerializedName("цвет")
    val color: String
)