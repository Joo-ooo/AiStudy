package com.example.aistudy.utils

import androidx.compose.ui.graphics.Color
import com.example.aistudy.ui.theme.*

fun noteItemColor(index: Int): Color {
    val listOfColors = listOf<Color>(
        RichBrilliantLavender, LightSalmonPink, LightGreen, PastelYellow, Waterspout, PaleViolet
    )

    val colorIndex = index % listOfColors.size

    return listOfColors[colorIndex]
}