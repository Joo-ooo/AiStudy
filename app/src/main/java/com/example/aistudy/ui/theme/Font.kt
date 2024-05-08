package com.example.aistudy.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.aistudy.R
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

val fontFamily = FontFamily(
    Font(R.font.nunito_light300, weight = FontWeight.W300),
    Font(R.font.nunito_regular400, weight = FontWeight.W400),
    Font(R.font.nunito_medium500, weight = FontWeight.W500),
    Font(R.font.nunito_semibold600, weight = FontWeight.W600),
    Font(R.font.nunito_bold700, weight = FontWeight.W700),
    Font(R.font.nunito_extrabold800, weight = FontWeight.W800),
    Font(R.font.nunito_black900, weight = FontWeight.W900)
)

// chat
val Typography2 = Typography(
    bodyLarge = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ))