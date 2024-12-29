import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class EventManagement {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EventManagementScreen(
        festivalId: Long,
        festivalName: String,
        onEventAdded: () -> Unit,
        onFestivalEdit: () -> Unit,
        onFestivalDelete: () -> Unit
    ) {
        var events by remember { mutableStateOf(listOf<Event>()) }
        var isLoading by remember { mutableStateOf(false) }
        var showAddEventScreen by remember { mutableStateOf(false) }
        var showEditEventScreen by remember { mutableStateOf<Event?>(null) }
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            isLoading = true
            try {
                events = fetchEventsFromApi(festivalId)
            } catch (e: Exception) {
                events = emptyList()
            } finally {
                isLoading = false
            }
        }

        when {
            showAddEventScreen -> AddEventScreen(
                festivalId = festivalId,
                onEventAdded = {
                    showAddEventScreen = false
                    coroutineScope.launch {
                        isLoading = true
                        try {
                            events = fetchEventsFromApi(festivalId)
                        } catch (e: Exception) {
                            events = emptyList()
                        } finally {
                            isLoading = false
                        }
                    }
                    onEventAdded()
                }
            )

            showEditEventScreen != null -> EditEventScreen(
                event = showEditEventScreen!!,
                onEventUpdated = {
                    showEditEventScreen = null
                    coroutineScope.launch {
                        isLoading = true
                        try {
                            events = fetchEventsFromApi(festivalId)
                        } catch (e: Exception) {
                            events = emptyList()
                        } finally {
                            isLoading = false
                        }
                    }
                }
            )

            else -> Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(text = festivalName) },
                        actions = {
                            var expandedMenu by remember { mutableStateOf(false) }
                            IconButton(onClick = { expandedMenu = !expandedMenu }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More Options")
                            }
                            DropdownMenu(
                                expanded = expandedMenu,
                                onDismissRequest = { expandedMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Add Event") },
                                    onClick = {
                                        expandedMenu = false
                                        showAddEventScreen = true
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Edit Festival") },
                                    onClick = {
                                        expandedMenu = false
                                        onFestivalEdit()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Delete Festival") },
                                    onClick = {
                                        expandedMenu = false
                                        onFestivalDelete()
                                    }
                                )
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
                        Text(
                            text = "Manage Events",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        if (isLoading) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else if (events.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "No events available", style = MaterialTheme.typography.bodyLarge)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(events) { event ->
                                    EventCard(
                                        event = event,
                                        onEditEvent = { showEditEventScreen = it },
                                        onDeleteEvent = {
                                            coroutineScope.launch {
                                                deleteEvent(it.id ?: 0)
                                                events = fetchEventsFromApi(festivalId)
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
    fun EventCard(
        event: Event,
        onEditEvent: (Event) -> Unit,
        onDeleteEvent: (Event) -> Unit
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
                    text = "Name: ${event.name}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Time: ${event.time}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Location: ${event.latitude}, ${event.longitude}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = { onEditEvent(event) }) {
                        Text("Edit")
                    }
                    TextButton(onClick = { onDeleteEvent(event) }) {
                        Text("Delete")
                    }
                }
            }
        }
    }

    @Composable
    fun AddEventScreen(
        festivalId: Long,
        onEventAdded: () -> Unit
    ) {
        var eventName by remember { mutableStateOf("") }
        var latitude by remember { mutableStateOf("") }
        var longitude by remember { mutableStateOf("") }
        var address by remember { mutableStateOf("") }
        var date by remember { mutableStateOf("") }
        var time by remember { mutableStateOf("") }
        var imageUri by remember { mutableStateOf<String?>(null) }
        var isLoading by remember { mutableStateOf(false) }

        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Add Event",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = eventName,
                onValueChange = { eventName = it },
                label = { Text("Event Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = latitude,
                onValueChange = { latitude = it },
                label = { Text("Latitude") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = longitude,
                onValueChange = { longitude = it },
                label = { Text("Longitude") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val calendar = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            val selectedDate = Calendar.getInstance().apply {
                                set(year, month, dayOfMonth)
                            }
                            date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (date.isEmpty()) "Select Date" else "Date: $date")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val calendar = Calendar.getInstance()
                    TimePickerDialog(
                        context,
                        { _, hour, minute ->
                            time = String.format("%02d:%02d", hour, minute)
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    ).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (time.isEmpty()) "Select Time" else "Time: $time")
            }

            Spacer(modifier = Modifier.height(8.dp))

            ImagePicker { uri ->
                coroutineScope.launch {
                    imageUri = uploadImageToSupabase(context, uri)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        try {
                            addEventToApi(
                                Event(
                                    id = null,
                                    festival_id = festivalId,
                                    name = eventName,
                                    latitude = latitude.toDouble(),
                                    longitude = longitude.toDouble(),
                                    address = address,
                                    time = "$date $time",
                                    image_url = imageUri ?: ""
                                )
                            )
                            onEventAdded()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (isLoading) "Adding..." else "Add Event")
            }
        }
    }

    @Composable
    fun EditEventScreen(
        event: Event,
        onEventUpdated: () -> Unit
    ) {
        var eventName by remember { mutableStateOf(event.name) }
        var latitude by remember { mutableStateOf(event.latitude.toString()) }
        var longitude by remember { mutableStateOf(event.longitude.toString()) }
        var address by remember { mutableStateOf(event.address) }

        // Tarih ve saat ayrıştırma
        val timeParts = event.time.split(" ")
        val initialDate = timeParts.getOrNull(0) ?: "" // Tarih kısmı
        val initialTime = timeParts.getOrNull(1) ?: "" // Saat kısmı
        var date by remember { mutableStateOf(initialDate) }
        var time by remember { mutableStateOf(initialTime) }

        var imageUri by remember { mutableStateOf<String?>(event.image_url) }
        var isLoading by remember { mutableStateOf(false) }

        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Edit Event",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = eventName,
                onValueChange = { eventName = it },
                label = { Text("Event Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = latitude,
                onValueChange = { latitude = it },
                label = { Text("Latitude") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = longitude,
                onValueChange = { longitude = it },
                label = { Text("Longitude") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val calendar = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            val selectedDate = Calendar.getInstance().apply {
                                set(year, month, dayOfMonth)
                            }
                            date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (date.isEmpty()) "Select Date" else "Date: $date")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val calendar = Calendar.getInstance()
                    TimePickerDialog(
                        context,
                        { _, hour, minute ->
                            time = String.format("%02d:%02d", hour, minute)
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    ).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (time.isEmpty()) "Select Time" else "Time: $time")
            }

            Spacer(modifier = Modifier.height(8.dp))

            ImagePicker { uri ->
                coroutineScope.launch {
                    imageUri = uploadImageToSupabase(context, uri)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        try {
                            updateEventInApi(
                                event.copy(
                                    name = eventName,
                                    latitude = latitude.toDouble(),
                                    longitude = longitude.toDouble(),
                                    address = address,
                                    time = "$date $time",
                                    image_url = imageUri ?: ""
                                )
                            )
                            onEventUpdated()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (isLoading) "Updating..." else "Update Event")
            }
        }
    }

    private suspend fun updateEventInApi(event: Event) {
        try {
            val stageIdQuery = "eq.${event.id ?: throw IllegalArgumentException("Event ID is null")}"
            SupabaseClient.service.updateEvent(
                stageId = stageIdQuery,
                apiKey = SupabaseClient.API_KEY,
                stage = event
            )
            println("Event updated successfully.")
        } catch (e: retrofit2.HttpException) {
            println("HTTP Error: ${e.response()?.errorBody()?.string()}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun deleteEvent(eventId: Long) {
        try {
            if (eventId <= 0) {
                println("Invalid eventId: $eventId")
                return
            }

            println("Deleting Event with ID: $eventId")
            SupabaseClient.service.deleteEvent(
                id = "eq.$eventId", // Parametre doğru şekilde iletiliyor
                apiKey = SupabaseClient.API_KEY
            )
            println("Event deleted successfully.")
        } catch (e: retrofit2.HttpException) {
            println("HTTP Error: ${e.response()?.errorBody()?.string()}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    @Composable
    fun ImagePicker(onImagePicked: (Uri) -> Unit) {
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let { onImagePicked(it) }
        }

        Button(onClick = { launcher.launch("image/*") }) {
            Text("Pick Image")
        }
    }

    private suspend fun uploadImageToSupabase(context: Context, uri: Uri): String? {
        return try {
            val inputStream: InputStream = context.contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("temp_upload", ".jpg", context.cacheDir)
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }

            val fileName = "event_images/${UUID.randomUUID()}.jpg"
            val requestFile = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData("file", tempFile.name, requestFile)

            SupabaseClient.service.uploadImage(
                bucket = "event_images",
                path = fileName,
                file = multipartBody,
                apiKey = SupabaseClient.API_KEY
            )

            SupabaseClient.service.getPublicUrl(
                bucket = "event_images",
                path = fileName,
                apiKey = SupabaseClient.API_KEY
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun addEventToApi(stage: Event) {
        try {
            SupabaseClient.service.addEvent(
                SupabaseClient.API_KEY,
                stage
            )
        } catch (e: retrofit2.HttpException) {
            println("HTTP Error: ${e.response()?.errorBody()?.string()}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun fetchEventsFromApi(festivalId: Long): List<Event> {
        try {
            val events = SupabaseClient.service.getEvents(
                filter = "eq.$festivalId",
                apiKey = SupabaseClient.API_KEY
            )
            println("Fetched Events: $events")
            return events
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }
}