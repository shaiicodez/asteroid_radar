package com.udacity.asteroidradar

import java.text.SimpleDateFormat
import java.util.*

//Formulas for date conversion
// API conversion methods

fun getDaysFromNowDate(daysFromToday: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, daysFromToday)

    return calendar.time
}

fun getDaysFromNowStr(daysFromToday: Int, pattern: String = Constants.API_QUERY_DATE_FORMAT): String{
    val sdf = SimpleDateFormat(pattern)
    return sdf.format(getDaysFromNowDate(daysFromToday))
}

fun dateToStr(date: Date, pattern: String = Constants.API_QUERY_DATE_FORMAT): String{
    val sdf = SimpleDateFormat(pattern)
    return sdf.format(date)
}

fun strToDate(dateStr: String, pattern: String = Constants.API_QUERY_DATE_FORMAT): Date {
    val format = SimpleDateFormat(pattern)
    return format.parse(dateStr)
}