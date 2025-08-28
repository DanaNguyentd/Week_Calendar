package com.example.weekcalendar.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.weekcalendar.R

@Composable
fun SettingScreen(
    onExitClicked: () -> Unit,
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(LocalContext.current))
) {
    val savedCurrentLanguage by viewModel.currentLanguage.collectAsState()
    var currentLanguage by remember(savedCurrentLanguage) { mutableIntStateOf(savedCurrentLanguage) }

    val isMondayStartSaved by viewModel.isMondayStart.collectAsState() // true = Monday, false = Sunday
    var isMondayStart by remember(isMondayStartSaved) { mutableStateOf(isMondayStartSaved) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .border(
                    width = 2.dp,
                    color = Color(0xff512da8),
                    shape = RoundedCornerShape(8.dp) // optional
                )
                .padding(16.dp), // optional inner space,
            contentAlignment = Alignment.Center // Center contents
        ){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally // Optional: center items in Column
            ){
                // Setting language box
                Box(
                    contentAlignment = Alignment.Center
                ){
                    SettingLanguage(
                        currentLanguage,
                        updateCurrentLanguage = { currentLanguage = it}
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))
                // Setting start day box
                Box(
                    contentAlignment = Alignment.Center
                ){
                    StartDayOfWeek(
                        currentLanguage,
                        isMondayStart,
                        updateDayWeekStart = { isMondayStart = it}
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))
                //Save Button
                Box(
                    contentAlignment = Alignment.Center
                ){
                    OutlinedButton(
                        onClick = {
                            viewModel.saveLanguage(currentLanguage)
                            viewModel.saveStartDay(isMondayStart)
                            onExitClicked()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xFFF3E5F5), // background color
                            contentColor = Color(0xff512da8)
                        ),
                        border = BorderStroke(2.dp, Color(0xff512da8)),
                        modifier = Modifier
                    ) {
                        Text(text = LanguageListShow[currentLanguage][2],
                            style = MaterialTheme.typography.bodyLarge.copy( // base style
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color( 0xff651fff)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingLanguage(
    currentLanguage: Int,
    updateCurrentLanguage: (Int) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = LanguageListShow[currentLanguage][0],
            style = MaterialTheme.typography.titleMedium.copy( // base style
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = Color( 0xff651fff)
            )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(0.5f), // Half of the screen width
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MomentaryIconButton(
                unselectedImage = R.drawable.fast_rewind,
                selectedImage = R.drawable.fast_rewind_filled,
                stepDelay = 100L,
                onClick = {moveIndex(currentLanguage, LanguageItems.size, direction = -1, updateCurrentLanguage)},
                contentDescription = "Previous Week selected",
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Row(
                modifier = Modifier
                    .weight(4f)
                    .border(1.dp, color = Color(0xff512da8), shape = RoundedCornerShape(8.dp))
                    .padding(3.dp), // space inside the border,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                Text(text = LanguageItems[currentLanguage].code,
                    style = MaterialTheme.typography.bodyLarge.copy( // base style
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color( 0xff651fff)
                    )
                )
                Image(
                    painter = painterResource(id = LanguageItems[currentLanguage].flagResId),
                    contentDescription = "${LanguageItems[currentLanguage].code} Flag",
                    modifier = Modifier
                        .size(36.dp)
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            MomentaryIconButton(
                unselectedImage = R.drawable.fast_forward,
                selectedImage = R.drawable.fast_forward_filled,
                stepDelay = 100L,
                onClick = {moveIndex(currentLanguage, LanguageItems.size, direction = 1, updateCurrentLanguage)},
                contentDescription = "Next Week selected",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StartDayOfWeek(
    currentLanguage: Int,
    isMondayStart: Boolean,
    updateDayWeekStart : (Boolean) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = LanguageListShow[currentLanguage][1],
            style = MaterialTheme.typography.titleMedium.copy( // base style
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = Color( 0xff651fff)
            )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(0.5f), // Half of the screen width
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MomentaryIconButton(
                unselectedImage = R.drawable.fast_rewind,
                selectedImage = R.drawable.fast_rewind_filled,
                stepDelay = 100L,
                onClick = {updateDayWeekStart(!isMondayStart)},
                contentDescription = "Previous Week selected",
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Box(
                modifier = Modifier
                    .weight(4f)
                    .border(1.dp, color = Color(0xff512da8), shape = RoundedCornerShape(8.dp))
                    .padding(5.dp),// space inside the border,
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = if(isMondayStart)DayInWeek[currentLanguage][0]  else DayInWeek[currentLanguage][6],
                    style = MaterialTheme.typography.bodyLarge.copy( // base style
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color( 0xff651fff)
                    )
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            MomentaryIconButton(
                unselectedImage = R.drawable.fast_forward,
                selectedImage = R.drawable.fast_forward_filled,
                stepDelay = 100L,
                onClick = {updateDayWeekStart(!isMondayStart)},
                contentDescription = "Next Week selected",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private fun moveIndex(
    currentIndex: Int,
    maxIndex: Int,
    direction: Int,
    onMove: (Int) -> Unit
) {
    val newIndex = when (direction) {
        1  -> if (currentIndex + 1 >= maxIndex) 0 else currentIndex + 1
        -1 -> if (currentIndex - 1 < 0) maxIndex - 1 else currentIndex - 1
        else -> currentIndex // No change if direction is 0 or invalid
    }
    onMove(newIndex)
}