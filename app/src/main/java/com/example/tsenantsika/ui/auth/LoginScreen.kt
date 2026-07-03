package com.example.tsenantsika.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.tsenantsika.ui.common.PrimaryButton
import com.example.tsenantsika.ui.theme.TextDark
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onCreatePatronne: () -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var nom by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }

    LaunchedEffect(state.success) { if (state.success) onLoginSuccess() }
    LaunchedEffect(state.needsPatronne) { if (state.needsPatronne) onCreatePatronne() }

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Tsenantsika", style = MaterialTheme.typography.headlineLarge, color = TextDark)
        Spacer(Modifier.height(8.dp))
        Text("Gestion de boutique", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(40.dp))

        OutlinedTextField(
            value = nom, onValueChange = { nom = it },
            label = { Text("Nom") }, modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = pin, onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) pin = it },
            label = { Text("PIN (4 chiffres)") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
        )
        state.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(24.dp))
        PrimaryButton("Se connecter", onClick = { viewModel.login(nom, pin) }, enabled = !state.loading)
    }
}

@Composable
fun CreatePatronneScreen(onSuccess: () -> Unit, viewModel: LoginViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsState()
    var nom by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    LaunchedEffect(state.success) { if (state.success) onSuccess() }

    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("Créer le compte Patronne", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(value = nom, onValueChange = { nom = it }, label = { Text("Nom") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = pin, onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) pin = it },
            label = { Text("PIN") }, modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = confirm, onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) confirm = it },
            label = { Text("Confirmer PIN") }, modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
        )
        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp)) }
        Spacer(Modifier.height(24.dp))
        PrimaryButton("Créer", onClick = {
            if (pin != confirm) viewModel.setError("Les PIN ne correspondent pas")
            else viewModel.createPatronne(nom, pin)
        })
    }
}

@Composable
fun EditPinScreen(userId: Long, onDone: () -> Unit, modifier: Modifier = Modifier, viewModel: LoginViewModel = koinViewModel()) {
    val state by viewModel.pinState.collectAsState()
    var oldPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }

    LaunchedEffect(state.success) { if (state.success) onDone() }

    Column(modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text("Modifier mon PIN", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(value = oldPin, onValueChange = { if (it.length <= 4) oldPin = it },
            label = { Text("Ancien PIN") }, modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword))
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = newPin, onValueChange = { if (it.length <= 4) newPin = it },
            label = { Text("Nouveau PIN") }, modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword))
        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp)) }
        Spacer(Modifier.height(24.dp))
        PrimaryButton("Enregistrer", onClick = { viewModel.changePin(userId, oldPin, newPin) })
    }
}
