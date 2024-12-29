package com.example.festivalapp

import AdminDashboardScreen
import Event
import EventManagement
import Festival
import FestivalManagement
import LoginScreen
import UserDashboard
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isLoggedIn by remember { mutableStateOf(false) }
            var isAdmin by remember { mutableStateOf(false) }
            var currentScreen by remember { mutableStateOf("login") }
            var loggedInUserId by remember { mutableStateOf<Long?>(null) } // Kullanıcı ID'si
            var selectedFestival by remember { mutableStateOf<Festival?>(null) }
            var selectedEvent by remember { mutableStateOf<Event?>(null) }
            val userScheduledEvents = remember { mutableStateListOf<Event>() }
            val coroutineScope = rememberCoroutineScope()
            var events by remember { mutableStateOf<List<Event>>(emptyList()) }

            BackHandler(enabled = true) {
                when (currentScreen) {
                    "dashboard" -> currentScreen = "login"
                    "manageFestivals" -> currentScreen = "dashboard"
                    "addFestival" -> currentScreen = "manageFestivals"
                    "editFestival" -> currentScreen = "manageFestivals"
                    "eventManagement" -> currentScreen = "manageFestivals"
                    "userDashboard" -> currentScreen = "login"
                    "festivalDetails" -> currentScreen = "userDashboard"
                    "eventDetails" -> currentScreen = "festivalDetails"
                    "userProfile" -> currentScreen = "userDashboard"
                    else -> finish()
                }
            }

            when (currentScreen) {
                "login" -> {
                    LoginScreen().Show { userId, isAdminLogin ->
                        loggedInUserId = userId
                        isLoggedIn = true
                        isAdmin = isAdminLogin
                        currentScreen = if (isAdminLogin) "dashboard" else "userDashboard"
                    }
                }
                "dashboard" -> {
                    AdminDashboardScreen(
                        onViewFestivalsClick = {
                            currentScreen = "manageFestivals"
                        },
                        onAddFestivalClick = {
                            currentScreen = "addFestival"
                        }
                    )
                }
                "manageFestivals" -> {
                    FestivalManagement().ManageFestivalsScreen(
                        onAddFestivalClick = {
                            currentScreen = "addFestival"
                        },
                        onFestivalSelected = { festival ->
                            selectedFestival = festival
                            currentScreen = "eventManagement"
                        },
                        onFestivalEdit = { festival ->
                            selectedFestival = festival
                            currentScreen = "editFestival"
                        },
                        onFestivalDelete = { festival ->
                            coroutineScope.launch {
                                FestivalManagement().deleteFestivalInApi(festival.id ?: 0)
                                currentScreen = "manageFestivals"
                            }
                        }
                    )
                }
                "addFestival" -> {
                    FestivalManagement().AddFestivalScreen(
                        onFestivalAdded = {
                            currentScreen = "manageFestivals"
                        }
                    )
                }
                "editFestival" -> {
                    selectedFestival?.let { festival ->
                        FestivalManagement().EditFestivalScreen(
                            festival = festival,
                            onFestivalUpdated = {
                                currentScreen = "manageFestivals"
                            }
                        )
                    } ?: Text("No Festival Selected")
                }
                "eventManagement" -> {
                    selectedFestival?.let { festival ->
                        EventManagement().EventManagementScreen(
                            festivalId = festival.id ?: 0,
                            festivalName = festival.name,
                            onEventAdded = {
                                currentScreen = "eventManagement"
                            },
                            onFestivalEdit = {
                                currentScreen = "editFestival"
                            },
                            onFestivalDelete = {
                                coroutineScope.launch {
                                    FestivalManagement().deleteFestivalInApi(festival.id ?: 0)
                                    currentScreen = "manageFestivals"
                                }
                            }
                        )
                    } ?: Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No Festival Selected")
                    }
                }
                "userDashboard" -> {
                    UserDashboard().UserDashboardScreen(
                        onFestivalClick = { festival ->
                            selectedFestival = festival
                            coroutineScope.launch {
                                // Etkinlikleri API'den çek
                                try {
                                    events = SupabaseClient.service.getEvents(
                                        filter = "eq.${festival.id}",
                                        apiKey = SupabaseClient.API_KEY
                                    )
                                    currentScreen = "festivalDetails"
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    events = emptyList()
                                }
                            }
                        },
                        onProfileClick = {
                            currentScreen = "userProfile"
                        }
                    )
                }
                "userProfile" -> {
                    loggedInUserId?.let { userId ->
                        UserDashboard().UserProfileScreen(
                            userId = userId,
                            supabaseService = SupabaseClient.service,
                            onBack = { currentScreen = "userDashboard" }
                        )
                    } ?: Text("User ID not available.")
                }
                "festivalDetails" -> {
                    selectedFestival?.let { festival ->
                        UserDashboard().FestivalDetailsScreen(
                            festival = festival,
                            events = events, // API'den gelen etkinlikler burada sağlanıyor
                            onEventClick = { event ->
                                selectedEvent = event
                                currentScreen = "eventDetails"
                            }
                        )
                    } ?: Text("No Festival Selected")
                }
                "eventDetails" -> {
                    selectedEvent?.let { event ->
                        loggedInUserId?.let { userId ->
                            UserDashboard().EventDetailsScreen(
                                event = event,
                                userId = userId,
                                supabaseService = SupabaseClient.service,
                                onAddToSchedule = {
                                    userScheduledEvents.add(event)
                                    currentScreen = "festivalDetails"
                                }
                            )
                        } ?: Text("User ID not available.")
                    } ?: Text("No Event Selected")
                }
                else -> {
                    Text("Unknown screen")
                }
            }
        }
    }
}