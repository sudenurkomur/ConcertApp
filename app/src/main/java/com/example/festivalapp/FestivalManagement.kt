import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class FestivalManagement {

    @Composable
    fun ManageFestivalsScreen(
        onAddFestivalClick: () -> Unit,
        onFestivalSelected: (Festival) -> Unit,
        onFestivalEdit: (Festival) -> Unit,
        onFestivalDelete: (Festival) -> Unit
    ) {
        var festivals by remember { mutableStateOf(listOf<Festival>()) }
        var isLoading by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()

        // Fetch festivals
        LaunchedEffect(Unit) {
            isLoading = true
            try {
                festivals = fetchFestivalsFromApi()
            } catch (e: Exception) {
                festivals = emptyList()
            } finally {
                isLoading = false
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Manage Festivals",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(
                onClick = onAddFestivalClick,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(bottom = 16.dp)
            ) {
                Text("Add Festival")
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (festivals.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No active festivals", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(festivals.size) { index ->
                        val festival = festivals[index]
                        FestivalCard(
                            festival = festival,
                            onFestivalClick = { onFestivalSelected(festival) },
                            onEditClick = { onFestivalEdit(festival) },
                            onDeleteClick = { onFestivalDelete(festival) }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun AddFestivalScreen(onFestivalAdded: () -> Unit) {
        var name by remember { mutableStateOf("") }
        var startDate by remember { mutableStateOf("") }
        var endDate by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
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
                text = "Add Festival",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Festival Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                val calendar = Calendar.getInstance()
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        val selectedDate = Calendar.getInstance().apply {
                            set(year, month, dayOfMonth)
                        }
                        startDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }) {
                Text(if (startDate.isEmpty()) "Select Start Date" else "Start Date: $startDate")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                val calendar = Calendar.getInstance()
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        val selectedDate = Calendar.getInstance().apply {
                            set(year, month, dayOfMonth)
                        }
                        endDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }) {
                Text(if (endDate.isEmpty()) "Select End Date" else "End Date: $endDate")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        try {
                            addFestivalToApi(
                                Festival(
                                    id = null,
                                    name = name,
                                    start_date = startDate,
                                    end_date = endDate,
                                    description = description
                                )
                            )
                            onFestivalAdded()
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
                Text(text = if (isLoading) "Adding..." else "Add Festival")
            }
        }
    }

    @Composable
    fun EditFestivalScreen(festival: Festival, onFestivalUpdated: () -> Unit) {
        var name by remember { mutableStateOf(festival.name) }
        var startDate by remember { mutableStateOf(festival.start_date) }
        var endDate by remember { mutableStateOf(festival.end_date) }
        var description by remember { mutableStateOf(festival.description ?: "") }
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
                text = "Edit Festival",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Festival Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                val calendar = Calendar.getInstance()
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        startDate = "$year-${month + 1}-$dayOfMonth"
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }) {
                Text(if (startDate.isEmpty()) "Select Start Date" else "Start Date: $startDate")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                val calendar = Calendar.getInstance()
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        endDate = "$year-${month + 1}-$dayOfMonth"
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }) {
                Text(if (endDate.isEmpty()) "Select End Date" else "End Date: $endDate")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        try {
                            val updatedFestival = festival.copy(
                                name = name,
                                start_date = startDate,
                                end_date = endDate,
                                description = description
                            )
                            updateFestivalInApi(updatedFestival)
                            onFestivalUpdated()
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
                Text(text = if (isLoading) "Updating..." else "Update Festival")
            }
        }
    }

    @Composable
    fun FestivalCard(
        festival: Festival,
        onFestivalClick: () -> Unit,
        onEditClick: () -> Unit,
        onDeleteClick: () -> Unit
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
                    text = "Name: ${festival.name}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Start Date: ${festival.start_date}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "End Date: ${festival.end_date}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Description: ${festival.description.orEmpty()}",
                    style = MaterialTheme.typography.bodySmall
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onEditClick) {
                        Text("Edit")
                    }
                    TextButton(onClick = onDeleteClick) {
                        Text("Delete")
                    }
                }
            }
        }
    }

    private suspend fun fetchFestivalsFromApi(): List<Festival> {
        return try {
            SupabaseClient.service.getFestivals(SupabaseClient.API_KEY)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private suspend fun updateFestivalInApi(festival: Festival) {
        try {
            val festivalId = festival.id ?: throw IllegalArgumentException("Festival ID is null")
            println("Updating Festival with ID: $festivalId")

            SupabaseClient.service.updateFestival(
                idFilter = "eq.$festivalId",
                apiKey = SupabaseClient.API_KEY,
                festival = festival
            )
            println("Festival updated successfully.")
        } catch (e: retrofit2.HttpException) {
            println("HTTP Error: ${e.response()?.errorBody()?.string()}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun addFestivalToApi(festival: Festival) {
        try {
            SupabaseClient.service.addFestival(
                SupabaseClient.API_KEY,
                festival
            )
        } catch (e: retrofit2.HttpException) {
            println("HTTP Error: ${e.response()?.errorBody()?.string()}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteFestivalInApi(festivalId: Long) {
        try {
            if (festivalId <= 0) {
                println("Invalid festivalId: $festivalId")
                return
            }

            println("Deleting Festival with ID: $festivalId")
            SupabaseClient.service.deleteFestival(
                idFilter = "eq.$festivalId", // Supabase REST API'ye uygun parametre
                apiKey = SupabaseClient.API_KEY
            )
            println("Festival deleted successfully.")
        } catch (e: retrofit2.HttpException) {
            println("HTTP Error: ${e.response()?.errorBody()?.string()}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}