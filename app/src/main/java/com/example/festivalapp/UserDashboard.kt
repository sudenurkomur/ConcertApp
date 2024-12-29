import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale

class UserDashboard {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun UserDashboardScreen(
        onFestivalClick: (Festival) -> Unit,
        onProfileClick: () -> Unit
    ) {
        var festivals by remember { mutableStateOf(listOf<Festival>()) }
        var isLoading by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            isLoading = true
            try {
                festivals = SupabaseClient.service.getFestivals(SupabaseClient.API_KEY)
            } catch (e: Exception) {
                e.printStackTrace()
                festivals = emptyList()
            } finally {
                isLoading = false
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("User Dashboard") },
                    actions = {
                        IconButton(onClick = onProfileClick) {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                        }
                    }
                )
            },
            content = { paddingValues ->
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Available Festivals",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(festivals) { festival ->
                                FestivalCard(
                                    festival = festival,
                                    onFestivalClick = { onFestivalClick(festival) }
                                )
                            }
                        }
                    }
                }
            }
        )
    }

    @Composable
    fun FestivalDetailsScreen(
        festival: Festival,
        events: List<Event>,
        onEventClick: (Event) -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Events in ${festival.name}",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (events.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No events available for this festival.")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(events) { event ->
                        EventCard(event = event, onEventClick = { onEventClick(event) })
                    }
                }
            }
        }
    }



    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EventDetailsScreen(
        event: Event,
        userId: Long,
        supabaseService: SupabaseService,
        onAddToSchedule: () -> Unit
    ) {
        val coroutineScope = rememberCoroutineScope()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Event Details") },
                    navigationIcon = {
                        IconButton(onClick = onAddToSchedule) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    // Event Name
                    Text(
                        text = "Event Name: ${event.name ?: "Unknown"}",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Event Date and Time
                    val date = event.time.split(" ").getOrNull(0) ?: "Unknown Date"
                    val time = event.time.split(" ").getOrNull(1) ?: "Unknown Time"
                    Text(
                        text = "Date: $date",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Event Address
                    Text(
                        text = "Address: ${event.address ?: "No Address Available"}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Google Maps Placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Google Maps placeholder for event location.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Save Event Button
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    supabaseService.saveEvent(
                                        apiKey = SupabaseClient.API_KEY,
                                        saveEventRequest = SaveEventRequest(
                                            user_id = userId,
                                            event_id = event.id ?: 0
                                        )
                                    )
                                    println("Event saved successfully!")
                                } catch (e: Exception) {
                                    println("Error saving event: ${e.message}")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Event")
                    }
                }
            }
        )
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun UserProfileScreen(
        userId: Long,
        supabaseService: SupabaseService,
        onBack: () -> Unit
    ) {
        val coroutineScope = rememberCoroutineScope()
        val userSavedEvents = remember { mutableStateListOf<Event>() }
        var showPlaceholder by remember { mutableStateOf(false) } // Placeholder için durum

        // Kullanıcıya ait etkinlikleri detaylı bir şekilde çekme
        LaunchedEffect(userId) {
            coroutineScope.launch {
                try {
                    val savedEvents = supabaseService.getUserSavedEvents(
                        userId = "eq.$userId", // String formatında
                        apiKey = SupabaseClient.API_KEY
                    )
                    userSavedEvents.clear()

                    // Her event_id için detay çekme
                    savedEvents.forEach { savedEvent ->
                        val eventDetails = supabaseService.getEventById(
                            eventIdFilter = "eq.${savedEvent.event_id}",
                            apiKey = SupabaseClient.API_KEY
                        ).firstOrNull()

                        if (eventDetails != null) {
                            userSavedEvents.add(eventDetails)
                        } else {
                            println("Event not found for event_id: ${savedEvent.event_id}")
                            // Silinmiş etkinlikleri temizleme
                            supabaseService.deleteSavedEvent(
                                userId = "eq.$userId",
                                eventId = "eq.${savedEvent.event_id}",
                                apiKey = SupabaseClient.API_KEY
                            )
                        }
                    }
                } catch (e: Exception) {
                    println("Error fetching saved events with details: ${e.message}")
                }
            }
        }

        if (showPlaceholder) {
            MapRoutePlaceholderScreen(onBack = { showPlaceholder = false }) // Placeholder ekranı
        } else {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("My Profile") },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        }
                    )
                },
                content = { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp)
                    ) {
                        // Rotayı Göster Butonu
                        Button(
                            onClick = { showPlaceholder = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            Text("Rotayı Göster")
                        }

                        Text(
                            text = "My Scheduled Events",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        if (userSavedEvents.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No events scheduled.", style = MaterialTheme.typography.bodyLarge)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(userSavedEvents) { event ->
                                    EventCardWithDelete(
                                        event = event,
                                        onDelete = {
                                            coroutineScope.launch {
                                                try {
                                                    supabaseService.deleteSavedEvent(
                                                        userId = "eq.$userId",
                                                        eventId = "eq.${event.id}",
                                                        apiKey = SupabaseClient.API_KEY
                                                    )
                                                    userSavedEvents.remove(event)
                                                } catch (e: Exception) {
                                                    println("Error deleting event: ${e.message}")
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }
    }

    @Composable
    fun EventCardWithDelete(
        event: Event,
        onDelete: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Event Name: ${event.name ?: "N/A"}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Date: ${event.time?.split(" ")?.getOrNull(0) ?: "N/A"}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Time: ${event.time?.split(" ")?.getOrNull(1) ?: "N/A"}",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onDelete,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Delete")
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MapRoutePlaceholderScreen(onBack: () -> Unit) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Route Placeholder") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            },
            content = { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Here we will show the route on Google Maps.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        )
    }

    @Composable
    fun MapViewComponent(lat: Double? = null, lng: Double? = null, waypoints: List<LatLng>? = null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Text("Google Maps not available")
        }
    }

    @Composable
    fun EventCard(
        event: Event,
        onEventClick: () -> Unit
    ) {
        val timeParts = event.time.split(" ")

        val date = timeParts.getOrNull(0) ?: "Unknown Date"
        val time = timeParts.getOrNull(1) ?: "Unknown Time"

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            onClick = { onEventClick() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Event Name: ${event.name}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Date: $date",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Time: $time",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }


    @Composable
    fun FestivalCard(
        festival: Festival,
        onFestivalClick: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            onClick = { onFestivalClick() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Festival Name: ${festival.name}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Start Date: ${festival.start_date}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "End Date: ${festival.end_date}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}