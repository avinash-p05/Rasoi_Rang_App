package com.example.recipesharingappxml.common

import java.util.Calendar

fun getCurrentMonth(): Int {
    val calendar = Calendar.getInstance()
    return calendar.get(Calendar.MONTH) + 1 // Adding 1 because Calendar.MONTH is zero-based
}

fun getCurrentDay(): Int {
    val calendar = Calendar.getInstance()
    return calendar.get(Calendar.DAY_OF_MONTH)
}

fun getCurrentWeek(): Int {
    val calendar = Calendar.getInstance()
    return calendar.get(Calendar.WEEK_OF_YEAR)
}