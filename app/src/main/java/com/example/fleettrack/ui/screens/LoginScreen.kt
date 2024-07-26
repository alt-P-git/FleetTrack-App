package com.example.fleettrack.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fleettrack.model.UserCredentials
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    failedAttempt: Boolean = false,
    modifier: Modifier = Modifier
) {
    val viewModel: FleetTrackViewModel = viewModel(
        factory = FleetTrackViewModel.Factory
    )
    var userCredentials: UserCredentials by remember { mutableStateOf(UserCredentials("", "")) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FleetTrack | Login") },
            )
        }
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues = it),
        ) {
            Spacer(modifier = Modifier.weight(4f))
            UserCredentialsForm(
                userCredentials = userCredentials,
                onChange = { userCredentials = it },
                onSubmit = { viewModel.loginAndCheckCredentials(userCredentials = userCredentials) },
                failedAttempt = failedAttempt,
                modifier = modifier
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun UserCredentialsForm(
    userCredentials: UserCredentials,
    onChange: (UserCredentials) -> Unit,
    onSubmit: () -> Unit,
    failedAttempt: Boolean = false,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Please enter your credentials to login.", modifier = modifier.padding(8.dp), fontSize = 12.sp)
        OutlinedTextField(
            value = userCredentials.userid,
            onValueChange = { onChange(userCredentials.copy(userid = it)) },
            label = { Text("Username") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            modifier = modifier
                .padding(8.dp)
        )

        OutlinedTextField(
            value = userCredentials.password,
            onValueChange = { onChange(userCredentials.copy(password = it)) },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password
            ),
        )
        Button(
            onClick = onSubmit,
            modifier = modifier
                .padding(top = 16.dp)
        ) {
            Text(text = "Login")
        }
        if(failedAttempt) {
            Text(
                text = "Login failed. Please try again.",
                color = MaterialTheme.colorScheme.error,
                modifier = modifier
                    .padding(top = 8.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(modifier = Modifier)
}