package com.udacity.asteroidradar

import java.text.SimpleDateFormat
import java.util.*

//use dummy dates for testing
val startDate = "2019-09-14"
val endDate = "2021-01-11"

//get system date
fun getDate(days : Int) : Date{
    val calender = Calendar.getInstance()
    calender.add(Calendar.DAY_OF_YEAR, days)

    return calender.time
}

//convert the period timezone
fun getPeriodTimeZone(days : Int, pattern : String = Constants.API_QUERY_DATE_FORMAT): String{
    val simpleFormat = SimpleDateFormat(pattern)
    return simpleFormat.format(getDate(days))
}

//convert date to string
fun dateToString(date: Date, pattern: String = Constants.API_QUERY_DATE_FORMAT) : String{
    val simpleFormat = SimpleDateFormat(pattern)
    return simpleFormat.format(date)

}

//convert string to date
fun stringToDate(stringDate : String, pattern: String = Constants.API_QUERY_DATE_FORMAT) : Date {
    val format = SimpleDateFormat(pattern)
    return format.parse(stringDate)
}