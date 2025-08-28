package com.example.weekcalendar.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.weekcalendar.R

import java.time.DayOfWeek

import java.util.Calendar
import java.util.Locale

@Composable
fun MainScreen(
    onSettingClicked: () -> Unit,
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(LocalContext.current))
) {
    val savedCurrentLanguage by viewModel.currentLanguage.collectAsState()
    val currentLanguage by remember(savedCurrentLanguage) { mutableIntStateOf(savedCurrentLanguage) }

    val isMondayStartSaved by viewModel.isMondayStart.collectAsState() // true = Monday, false = Sunday
    val isMondayStart by remember(isMondayStartSaved) { mutableStateOf(isMondayStartSaved) }

    val calendar = Calendar.getInstance()
    var currentYear by remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }
    var currentMonth by remember { mutableIntStateOf(calendar.get(Calendar.MONTH)) }
    var currentDate by remember { mutableIntStateOf(calendar.get(Calendar.DATE)) }
    var currentWeek by remember(isMondayStart) {
        mutableIntStateOf(getWeekOfYear(isMondayStart, currentYear, currentMonth, currentDate))
    }

    var weekList by remember {
        mutableStateOf(
            (1..if (is53WeekYear(currentYear)) 53 else 52).toList()
        )
    }
    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ){
        // Setting button on the top-right
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, end = 30.dp)
        ){
            IconButton(
                onClick = onSettingClicked,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .border(1.dp, Color.Gray, shape = CircleShape)
                    .size(48.dp) // Ensures it's circular and consistent
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
        }
        // Year Box
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val gradientColors = listOf(
                Color(0xFFE1F5FE), Color(0xFF4FC3F7),
                Color(0xFF0277BD), Color(0xFF0091EA)
            )
            GradientText(
                currentYear,
                55.sp,
                gradientColors,
                newYear = { currentYear = it },
                newWeekList = { weekList = it }
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        // Week Box
        TitleRow(
            currentLanguage,
            currentWeek,
            newWeek = { currentWeek = it },
            currentYear,
            newYear = { currentYear = it },
            weekList
        )

        Spacer(modifier = Modifier.height(15.dp))

        // main calendar table
        CalendarTable(
            isMondayStart,
            currentLanguage,
            currentWeek,
            currentYear
        )

        Spacer(modifier = Modifier.height(70.dp))

        // Insert day and find week contain that day
        FindDay(
            currentLanguage,
            isMondayStart,
            newWeek = { currentWeek = it },
            currentYear,
            currentDate,
            newDate = { currentDate = it },
            currentMonth,
            newMonth = { currentMonth = it },
            newYear = {currentYear = it}
        )

        Spacer(modifier = Modifier.height(5.dp))

        // Note row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ){
                Text(
                    text = LanguageListShow[currentLanguage][5],
                    color = Color( 0xffaa00ff)
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ){
                Text(
                    text = LanguageListShow[currentLanguage][6],
                    color = Color( 0xffaa00ff)
                )
            }
        }

    }
}

@Composable
fun FindDay(
    currentLanguage: Int,
    isMondayStart: Boolean,
    newWeek: (Int) -> Unit,
    currentYear: Int,
    currentDate: Int,
    newDate: (Int) -> Unit,
    currentMonth: Int,
    newMonth: (Int) -> Unit,
    newYear: (Int) -> Unit
) {
    val monthNames = LocaleUtils.getAllMonthNamesByLanguageId(currentLanguage)

    var dayList by remember { mutableStateOf(daysInMonth(currentMonth, currentYear)) }
    Box(
        modifier = Modifier
            .padding(start = 10.dp, end = 20.dp)
            .border(2.dp, color = Color(0xff512da8), RoundedCornerShape(8.dp))
            .background(Color(0xFFF3E5F5))
            .padding(5.dp),
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            TodayButton(
                newWeek,
                newYear,
                newDate,
                newMonth,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )

            RollingMonthInput(
                monthNames,
                monthNames[currentMonth],
                newMonth,
                newDayList = {dayList = it},
                currentYear,
                modifier = Modifier
                    .weight(2f)
                    .rightBorder(Color(0xff512da8), 10f)
            )

            RollingDayInput(
                dayList,
                currentDate,
                newDate,
                modifier = Modifier
                    .weight(1f)
            )
            FindButton(
                isMondayStart,
                currentDate,
                currentMonth,
                currentYear,
                newWeek,
                modifier = Modifier
                    .weight(2f)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun RollingWeekInput(
    inputList: List<Int>,
    defaultValue: Int,
    valueChange: (Int) -> Unit,
    textStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    RollingSelectorInput(
        inputList = inputList,
        selectedValue = defaultValue,
        onValueChange = valueChange,
        modifier = modifier,
        textAlign = TextAlign.Start,
        sharedTextStyle = textStyle
    )
}

@Composable
fun RollingMonthInput(
    inputList: List<String>,
    defaultValue: String,
    valueChange: (Int) -> Unit,
    newDayList : (List<Int>) -> Unit,
    currentYear: Int,
    modifier: Modifier = Modifier
) {
    RollingSelectorInput(
        inputList = inputList,
        selectedValue = defaultValue,
        onValueChange = {
            valueChange(inputList.indexOf(it))
            newDayList(daysInMonth(inputList.indexOf(it), currentYear))},
        modifier = modifier,
        textAlign = TextAlign.Center
    )
}

@Composable
fun RollingDayInput(
    inputList: List<Int>,
    defaultValue: Int,
    valueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    RollingSelectorInput(
        inputList = inputList,
        selectedValue = defaultValue,
        onValueChange = valueChange,
        modifier = modifier,
        textAlign = TextAlign.Start
    )
}

@Composable
fun FindButton(
    isMondayStart: Boolean,
    currentDate: Int,
    currentMonth: Int,
    currentYear: Int,
    newWeek: (Int) -> Unit,
    modifier: Modifier = Modifier,
){
    Box(
        modifier = modifier
            .padding(end = 10.dp)
    ) {
        Button(
            onClick = { newWeek(getWeekOfYear(isMondayStart, currentYear, currentMonth, currentDate)) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3E5F5)),
            modifier = Modifier
                .border(2.dp, Color(0xff512da8), RoundedCornerShape(8.dp))
                .align(Alignment.CenterEnd)
        ){
            Text(
                text = "F",
                style = MaterialTheme.typography.titleMedium,
                color = Color( 0xff651fff),
                fontSize = 25.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun TodayButton(
    newWeek: (Int) -> Unit,
    newYear: (Int) -> Unit,
    newDate: (Int) -> Unit,
    newMonth: (Int) -> Unit,
    modifier: Modifier = Modifier,
){
    Box(
        modifier = modifier
            .padding(start = 1.dp)
    ) {
        Button(
            onClick = {
                newWeek(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR))
                newYear(Calendar.getInstance().get(Calendar.YEAR))
                newDate(Calendar.getInstance().get(Calendar.DATE))
                newMonth(Calendar.getInstance().get(Calendar.MONTH))
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3E5F5)),
            modifier = Modifier
                .border(2.dp, Color(0xff512da8), RoundedCornerShape(8.dp))
                .align(Alignment.CenterEnd)
        ){
            Text(
                text = "T",
                style = MaterialTheme.typography.titleMedium,
                color = Color( 0xff651fff),
                fontSize = 25.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun TitleRow(
    currentLanguage: Int,
    currentWeek: Int,
    newWeek: (Int) -> Unit,
    currentYear: Int,
    newYear: (Int) -> Unit,
    weekList: List<Int>
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.5f), // Half of the screen width
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            MomentaryIconButton(
                unselectedImage = R.drawable.fast_rewind,
                selectedImage = R.drawable.fast_rewind_filled,
                stepDelay = 100L,
                onClick = {updateWeek(
                    currentWeek-1,
                    newWeek,
                    currentYear,
                    newYear
                )},
                contentDescription = "Previous Week selected",
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Row(
                modifier = Modifier.weight(4f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                Text(
                    text = LanguageListShow[currentLanguage][3],
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF263238),
                    fontSize = 25.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                RollingWeekInput(
                    weekList,
                    currentWeek,
                    newWeek,
                    MaterialTheme.typography.titleMedium.copy(
                        color = Color(0xFF263238),
                        fontSize = 25.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            MomentaryIconButton(
                unselectedImage = R.drawable.fast_forward,
                selectedImage = R.drawable.fast_forward_filled,
                stepDelay = 100L,
                onClick = {updateWeek(
                    currentWeek+1,
                    newWeek,
                    currentYear,
                    newYear
                )},
                contentDescription = "Next Week selected",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun CalendarTable(
    isMondayStart: Boolean,
    currentLanguage: Int,
    currentWeek: Int,
    currentYear: Int
){
    val dayName = if (isMondayStart) {
        listOf(
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY, DayOfWeek.SATURDAY,
            DayOfWeek.SUNDAY
        )
    } else {
        listOf(
            DayOfWeek.SUNDAY, DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        dayName.forEach { day ->
            Box(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp)
                    .border(
                        2.dp,
                        color = if (day == DayOfWeek.SUNDAY || day == DayOfWeek.SATURDAY) Color.Red else Color(
                            0xFF9E9E9E
                        ),
                        RoundedCornerShape(8.dp)
                    )
                    .background(Color(0xFFEEEEEE))
                    .padding(10.dp),
                contentAlignment = Alignment.TopStart
            ) {
                EachDay(isMondayStart, currentLanguage, day, currentWeek, currentYear)
            }
        }
    }
}

@Composable
fun EachDay(
    isMondayStart: Boolean,
    currentLanguage: Int,
    dayOfWeek: DayOfWeek,
    currentWeek: Int,
    currentYear: Int
) {
    val mDate = getDayOfWeek(isMondayStart, currentLanguage, dayOfWeek, currentWeek, currentYear)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,

        ) {
        val dayLetter = dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, Locale.ENGLISH).first().toString()
        DayBox(
            buildAnnotatedString {
                withStyle(style = SpanStyle(color = if (dayLetter == "S") Color(0xFFFF5252) else Color.Black)) {
                    append(dayLetter)
                }
            },
            color = if (dayLetter == "S") Color(0xFFFF5252) else Color.Black,
            24.sp,
            FontWeight.ExtraBold,
            modifier = Modifier.weight(1f),
            true
        )

        DayBox(
            buildAnnotatedString {
                withStyle(style = SpanStyle(color = if (dayLetter == "S") Color(0xFFFF5252) else Color.Black)) {
                    append(mDate[1])
                }
            },
            color = if (dayLetter == "S") Color(0xFFFF5252) else Color.Black,
            22.sp,
            FontWeight.ExtraBold,
            modifier = Modifier.weight(3f),
            true
        )

        if ( mDate[2].toBoolean()) {
            val mText = buildAnnotatedString {
                withStyle(style = SpanStyle(color = if (dayLetter == "S") Color(0xFFFF5252) else Color.Black)) {
                    append(mDate[0])
                }
                withStyle(style = SpanStyle(color = Color(0xFF0091EA))) {
                    append("  " + LanguageListShow[currentLanguage][4])
                }
            }
            DayBox(
                mText,
                color = if (dayLetter == "S") Color(0xFFFF5252) else Color.Black,
                24.sp,
                FontWeight.ExtraBold,
                modifier = Modifier.weight(4f),
                false
            )
        } else {
            DayBox(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(color = if (dayLetter == "S") Color(0xFFFF5252) else Color.Black)) {
                        append(mDate[0])
                    }
                },
                color = if (dayLetter == "S") Color(0xFFFF5252) else Color.Black,
                24.sp,
                FontWeight.ExtraBold,
                modifier = Modifier.weight(4f),
                false
            )
        }
    }
}

@Composable
fun DayBox(
    textStr: AnnotatedString,
    color: Color,
    textSize: TextUnit,
    fontWeight: FontWeight,
    modifier: Modifier = Modifier,  // accept modifier here
    isBorder: Boolean
){
    val mod = if (isBorder) {
        modifier.rightBorder(color, 10f)
    } else {
        modifier
    }
    Box(
        modifier = mod
            .padding(start = 5.dp),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = textStr,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 4.dp),
            textAlign = TextAlign.Center,
            fontSize = textSize,
            fontWeight = fontWeight
        )
    }
}

fun Modifier.rightBorder(
    color: Color,
    width: Float,
) = this.drawWithContent {
    drawContent()
    drawLine(
        color = color,
        start = Offset(size.width, 0f),
        end = Offset(size.width, size.height),
        strokeWidth = width,
    )
}

@Composable
fun GradientText(
    text: Int,
    textSize: TextUnit = 24.sp,
    colors: List<Color>,
    newYear : (Int) -> Unit,
    newWeekList : ((List<Int>) ) -> Unit
) {
    var currentYear by remember { mutableStateOf(text.toString()) }
    var clearOnNextInput by remember { mutableStateOf(false) }
    val hasClearedOnFocus by remember { mutableStateOf(false) }

    // Sync with external value (e.g. initial state)
    LaunchedEffect(text) {
        if (!clearOnNextInput && text.toString() != currentYear) {
            currentYear = text.toString()
        }
    }

    TextField(
        value = currentYear,
        onValueChange = { newValue ->
            var updatedValue = newValue
            // If this is the first input after focus, clear previous value
            if (clearOnNextInput) {
                updatedValue = newValue.lastOrNull()?.toString() ?: ""
                clearOnNextInput = false
            }

            // Allow only digits and max length 4
            if (updatedValue.length <= 4 && newValue.all { it.isDigit() }) {
                currentYear = updatedValue
                if (newValue.isNotEmpty()) {
                    val yearInt = newValue.toInt()
                    newYear(yearInt)
                    newWeekList(
                        (1..if (is53WeekYear(yearInt)) 53 else 52).toList()
                    )
                }
            }
        },
        modifier = Modifier.onFocusChanged { focusState ->
            if (focusState.isFocused  && !hasClearedOnFocus) {
                currentYear = ""
                clearOnNextInput = true
            }
        },
        textStyle = TextStyle(
            color = Color.Transparent
        ),
        colors = TextFieldDefaults.colors(
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
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true
    )
    // Border layer:
    Text(
        text = currentYear,
        style = TextStyle(
            color = Color.Black, // Stroke color/gradient
            fontSize = textSize,
            fontWeight = FontWeight.ExtraBold,
            drawStyle = Stroke(width = 10f) // Outline thickness
        )
    )
    // Top layer: FILL (main gradient)
    Text(
        text = currentYear,
        style = TextStyle(
            brush = Brush.linearGradient(colors = colors), // Fill gradient
            fontSize = textSize,
            fontWeight = FontWeight.ExtraBold
        )
    )
}