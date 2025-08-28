package com.example.weekcalendar.ui.theme

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

val LANGUAGE_ID = intPreferencesKey("language")

val MONDAY_START = booleanPreferencesKey("monday_start")

fun loadLanguageID(context: Context): Flow<Int> = context.dataStore.data
.map { preferences -> preferences[LANGUAGE_ID] ?: 0 }  // Default value = 0

fun loadStartDay(context: Context): Flow<Boolean> = context.dataStore.data
    .map { preferences -> preferences[MONDAY_START] ?: true }  // Default value = true


suspend fun saveLanguageId(context: Context, value: Int) {
    context.dataStore.edit { it[LANGUAGE_ID] = value }
}

suspend fun saveStartDay(context: Context, value: Boolean) {
    context.dataStore.edit { it[MONDAY_START] = value }
}