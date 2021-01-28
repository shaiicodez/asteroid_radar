package com.udacity.asteroidradar

import com.udacity.asteroidradar.database.AsteroidsDatabase
import java.text.SimpleDateFormat
import java.util.*

//Formulas for date conversion
// API conversion methods

val startDate = "2019-09-14"
val endDate = "2021-01-11"

fun AsteroidsDatabase.calculatePeriod(upToDays: Int) = asteroidDao.getAsteroids(getDate(daysFromToday = -1), getDate(daysFromToday = upToDays))
fun AsteroidsDatabase.getToday() = calculatePeriod(upToDays = 0)
fun AsteroidsDatabase.getUpToEndDate() = calculatePeriod(upToDays = Constants.DEFAULT_END_DATE_DAYS)

// get system current date
fun getDate(daysFromToday: Int): Date {
    val calendar = Calendar.getInstance() //fetch date from system calender
    calendar.add(Calendar.DAY_OF_YEAR, daysFromToday)

    return calendar.time
}

fun getDaysFromNowStr(daysFromToday: Int, pattern: String = Constants.API_QUERY_DATE_FORMAT): String{
    val sdf = SimpleDateFormat(pattern)
    return sdf.format(getDate(daysFromToday))
}

fun dateToStr(date: Date, pattern: String = Constants.API_QUERY_DATE_FORMAT): String{
    val sdf = SimpleDateFormat(pattern)
    return sdf.format(date)
}

fun strToDate(dateStr: String, pattern: String = Constants.API_QUERY_DATE_FORMAT): Date {
    val format = SimpleDateFormat(pattern)
    return format.parse(dateStr)
}