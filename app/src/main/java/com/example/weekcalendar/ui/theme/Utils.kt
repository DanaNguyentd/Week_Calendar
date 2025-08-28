package com.example.weekcalendar.ui.theme

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.weekcalendar.R
import kotlinx.coroutines.delay
import java.text.DateFormatSymbols
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.IsoFields
import java.time.temporal.WeekFields
import java.util.Calendar
import java.util.Locale

val LanguageItems = listOf(
    Lang("EN", R.drawable.uk_flag),
    Lang("VN", R.drawable.vn_flag),
    Lang("SE", R.drawable.se_flag)
)

val LanguageListShow = listOf(
//             0                  1                         2                 3       4           5                          6
    listOf("Choose Language", "First day of week"     , "Save and Exit"  , "Week" , "Today"  ,  "*** T - Today ***"  , "*** F - Find day ***"),
    listOf("Chọn ngôn ngữ"  , "Ngày đầu tiên của tuần", "Lưu và thoát"   , "Tuần" , "Hôm nay",  "*** T - Hôm nay ***", "*** F - Tìm ngày ***"),
    listOf("Välj språk"     , "Första dagen i veckan" , "Spara och stäng", "Vecka", "I dag"  , "*** T - Idag ***"    , "*** F - Hitta dag ***"),
)

val DayInWeek = listOf(
    listOf("MONDAY" , "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY" , "SATURDAY", "SUNDAY"  ),
    listOf("THỨ HAI", "THỨ BA" , "THỨ TƯ"   , "THỨ NĂM" , "THỨ SÁU", "THỨ BẢY" , "CHỦ NHẬT"),
    listOf("MÅNDAG" , "TISDAG" , "ONSDAG"   , "TORSDAG" , "FREDAG" , "LÖRDAG"  , "SÖNDAG"  )
)

data class Lang(
    val code: String,
    @DrawableRes val flagResId: Int
)

// Create the arrow next or forward
@Composable
fun MomentaryIconButton(
    unselectedImage: Int,
    selectedImage: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
    stepDelay: Long = 100L, // Minimum value is 1L milliseconds.
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressedListener by rememberUpdatedState(onClick)

    LaunchedEffect(isPressed) {
        while (isPressed) {
            delay(stepDelay.coerceIn(1L, Long.MAX_VALUE))
            pressedListener()
        }
    }

    IconButton(
        modifier = modifier,
        onClick = onClick,
        interactionSource = interactionSource
    ) {
        Icon(
            painter = if (isPressed) painterResource(id = selectedImage) else painterResource(id = unselectedImage),
            contentDescription = contentDescription,
        )
    }
}

// Foundation for rolling input
@Composable
fun <T> RollingSelectorInput(
    inputList: List<T>,
    selectedValue: T,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier,
    itemToString: (T) -> String = { it.toString() },
    textAlign: TextAlign = TextAlign.Center,
    sharedTextStyle: TextStyle = TextStyle(
        fontWeight = FontWeight.ExtraBold,
        textAlign = textAlign,
        fontSize = 20.sp,
        color = Color.Black
    )
) {
    var expanded by remember { mutableStateOf(false) }

    val transparentOutlineColors = TextFieldDefaults.colors(
        focusedTextColor = Color.Transparent,
        unfocusedTextColor = Color.Transparent,
        disabledTextColor = Color.Transparent,
        errorTextColor = Color.Transparent,

        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,

        cursorColor = Color.Transparent,
        selectionColors = TextSelectionColors(
            handleColor = Color.Transparent,
            backgroundColor = Color.Transparent
        ),

        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent
    )

    Box(modifier = modifier) {
        OutlinedTextField(
            value = itemToString(selectedValue),
            onValueChange = {},
            readOnly = true,
            enabled = false,
            modifier = Modifier.clickable { expanded = true },
            colors = transparentOutlineColors,
            textStyle = sharedTextStyle
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            inputList.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    },
                    text = {
                        Text(
                            text = itemToString(option),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color( 0xff263238),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                )
            }
        }
    }
}

fun updateWeek(
    currentWeek: Int,
    newWeek: (Int) -> Unit,
    currentYear: Int,
    newYear: (Int) -> Unit,
){
    if (currentWeek == 0) {
        newYear(currentYear - 1)
        newWeek(if (is53WeekYear(currentYear - 1)) 53 else 52)
    } else if ((currentWeek == 53 && !is53WeekYear(currentYear)) ||
        (currentWeek == 54 && is53WeekYear(currentYear))) {
        newWeek(1)
        newYear(currentYear + 1)
    } else {
        newWeek(currentWeek)
    }
}

fun is53WeekYear(year: Int): Boolean {
    val lastDayOfYear = LocalDate.of(year, 12, 31)
    val weekFields = WeekFields.ISO
    return lastDayOfYear.get(weekFields.weekOfWeekBasedYear()) == 53
}

fun getDayOfWeek(
    isMondayStart: Boolean,
    currentLanguage: Int,
    dayOfWeek: DayOfWeek,
    week: Int,
    year: Int
): List<String> {
    val date = if (isMondayStart) {
        LocalDate.of(year, 1, 1)
            .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week.toLong())
            .with(dayOfWeek)
    } else {
        val jan1 = LocalDate.of(year, 1, 1)
        val firstSunday = jan1.minusDays((jan1.dayOfWeek.value % 7).toLong())
        firstSunday.plusWeeks((week - 1).toLong())
            .plusDays((dayOfWeek.value % 7).toLong())
    }

    val day = date.dayOfMonth.toString()
    val month = LocaleUtils.getMonthNameByLanguageId(date, currentLanguage)  // You can replace this with date.month.getDisplayName(...) if needed
    val isToday = date == LocalDate.now()

    return listOf(day, month, isToday.toString())
}

object LocaleUtils {
    private fun getLocaleByLanguageId(languageId: Int): Locale {
        return when (languageId) {
            0 -> Locale.ENGLISH
            1 -> Locale("vi")        // Vietnamese
            2 -> Locale("sv")        // Swedish
            else -> Locale.ENGLISH   // fallback
        }
    }

    fun getMonthNameByLanguageId(date: LocalDate, languageId: Int): String {
        val locale = getLocaleByLanguageId(languageId)
        return date.month.getDisplayName(java.time.format.TextStyle.FULL, locale)
    }

    fun getAllMonthNamesByLanguageId(languageId: Int): List<String> {
        val locale = getLocaleByLanguageId(languageId)
        return DateFormatSymbols(locale).months
            .filter { it.isNotBlank() }
    }
}

fun daysInMonth(
    month: Int,
    year: Int = Calendar.getInstance().get(Calendar.YEAR)
): List<Int> {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, month)
    // Set to first day of the month
    calendar.set(Calendar.DAY_OF_MONTH, 1)

    val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    return (1..maxDay).toList()
}

fun getWeekOfYear(
    isMondayStart: Boolean,
    year: Int,
    month: Int,
    day: Int
): Int {
    val calendar = Calendar.getInstance().apply {
        firstDayOfWeek = if (isMondayStart) Calendar.MONDAY else Calendar.SUNDAY
        minimalDaysInFirstWeek = if (isMondayStart) 4 else 1
        set(year, month, day) // month is 0-based
    }
    return calendar.get(Calendar.WEEK_OF_YEAR)
}
