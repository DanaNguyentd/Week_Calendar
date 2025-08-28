package com.example.weekcalendar

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.weekcalendar.ui.theme.MainScreen
import com.example.weekcalendar.ui.theme.SettingScreen

/**
 * enum values that represent the screens in the app
 */

enum class WeekCalendarScreen(@StringRes val title:Int) {
    MainScreen(R.string.main),
    SettingScreen(R.string.setting)
}

@Composable
fun CalendarByWeekApp() {
    val isInPreview = LocalInspectionMode.current
    val navController = if (isInPreview) {
        // You can provide a dummy object or skip rendering the screen
        null
    } else {
        rememberNavController()
    }

    if (navController != null) {
        NavHost(
            navController = navController,
            startDestination = WeekCalendarScreen.MainScreen.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
        ) {
            composable(route = WeekCalendarScreen.MainScreen.name) {
                MainScreen(
                    onSettingClicked = {
                        navController.navigate(WeekCalendarScreen.SettingScreen.name)
                    }
                )
            }

            composable(route = WeekCalendarScreen.SettingScreen.name) {
                SettingScreen(
                    onExitClicked = {
                        navController.navigate(WeekCalendarScreen.MainScreen.name)
                    }
                )
            }
        }
    }
}