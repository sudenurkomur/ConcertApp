import androidx.compose.foundation.layout.* // Spacer, Column, Modifier
import androidx.compose.material3.* // Text, TextField, Button, MaterialTheme
import androidx.compose.runtime.* // remember, mutableStateOf
import androidx.compose.ui.Alignment // Alignment.CenterHorizontally
import androidx.compose.ui.Modifier // Modifier.fillMaxSize, Modifier.padding
import androidx.compose.ui.text.input.PasswordVisualTransformation // Şifre görünümü
import androidx.compose.ui.unit.dp // dp birimleri için
import kotlinx.coroutines.CoroutineScope // Coroutine işlemleri için
import kotlinx.coroutines.Dispatchers // Dispatcher türleri için
import kotlinx.coroutines.launch // Coroutine başlatmak için
import kotlinx.coroutines.withContext // Context değiştirmek için

class LoginScreen {

    @Composable
    fun Show(onLoginSuccess: (userId: Long, isAdminLogin: Boolean) -> Unit) {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var isLoginMode by remember { mutableStateOf(true) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Başlık
            Text(
                text = if (isLoginMode) "Login" else "Sign Up",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Kullanıcı adı
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Şifre
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Giriş veya Kayıt Butonu
            Button(
                onClick = {
                    if (isLoginMode) {
                        // Login İşlemi
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val response = SupabaseClient.service.getUser(
                                    username = "eq.$username",
                                    password = "eq.$password",
                                    apiKey = SupabaseClient.API_KEY
                                )

                                withContext(Dispatchers.Main) {
                                    if (response.isNotEmpty()) {
                                        val user = response.first()
                                        onLoginSuccess(user.id, user.role) // Kullanıcı ID ve rol
                                    } else {
                                        errorMessage = "Invalid credentials"
                                    }
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    errorMessage = "Error: ${e.message}"
                                }
                            }
                        }
                    } else {
                        // Sign Up İşlemi
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val newUser = SignUpUser(username = username, password = password, role = false)
                                SupabaseClient.service.signUpUser(newUser, SupabaseClient.API_KEY)
                                withContext(Dispatchers.Main) {
                                    errorMessage = "Sign up successful! Please log in."
                                    isLoginMode = true
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    errorMessage = "Sign up failed: ${e.message}"
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (isLoginMode) "Login" else "Sign Up")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mod Değiştirme
            TextButton(onClick = { isLoginMode = !isLoginMode }) {
                Text(text = if (isLoginMode) "Don't have an account? Sign Up" else "Already have an account? Login")
            }

            // Hata mesajı
            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}