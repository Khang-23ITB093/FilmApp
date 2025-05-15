package com.ericg.neatflix.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ericg.neatflix.R
import com.ericg.neatflix.model.RegisterRequest
import com.ericg.neatflix.screens.destinations.HomeDestination
import com.ericg.neatflix.screens.destinations.LogInScreenDestination
import com.ericg.neatflix.screens.destinations.SignUpScreenDestination
import com.ericg.neatflix.sharedComposables.BackButton
import com.ericg.neatflix.sharedComposables.NextButton
import com.ericg.neatflix.ui.theme.AppOnPrimaryColor
import com.ericg.neatflix.ui.theme.NeatFlixTheme
import com.ericg.neatflix.util.Resource
import com.ericg.neatflix.viewmodel.AuthViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@RootNavGraph
@Destination
@Composable
fun SignUpScreen(
    navigator: DestinationsNavigator,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var nameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    val registrationState by authViewModel.registrationState.collectAsState()

    LaunchedEffect(registrationState) {
        when (val state = registrationState) {
            is Resource.Success -> {
                if (state.data?.token != null) {
                    authViewModel.resetRegistrationState()
                    navigator.navigate(HomeDestination) {
                       popUpTo(SignUpScreenDestination.route) { inclusive = true } 
                       // Consider also popping LogInScreen if it's in the backstack from here
                       // navigator.popBackStack(LogInScreenDestination.route, inclusive = true, saveState = false)
                    }
                }
            }
            is Resource.Error -> {
                val errorMessage = state.statusMessage ?: "Registration Failed"
                android.widget.Toast.makeText(context, errorMessage, android.widget.Toast.LENGTH_LONG).show()
                authViewModel.resetRegistrationState()
            }
            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF180E36)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 24.dp)
        ) {
            BackButton {
                if (navigator.navigateUp()) {
                    // Successfully navigated up
                } else {
                    // Potentially close activity if this is the root of the back stack for this graph
                }
            }
            Text(
                text = "Create Account",
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 48.dp),
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.78F),
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            modifier = Modifier.padding(vertical = 12.dp),
            contentDescription = "logo"
        )

        val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = AppOnPrimaryColor,
            cursorColor = AppOnPrimaryColor,
            leadingIconColor = AppOnPrimaryColor,
            trailingIconColor = AppOnPrimaryColor,
            focusedBorderColor = Color.White,
            unfocusedBorderColor = AppOnPrimaryColor.copy(alpha = 0.5F),
            focusedLabelColor = Color.White,
            unfocusedLabelColor = AppOnPrimaryColor
        )

        OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            label = { Text("Full Name") },
            leadingIcon = { Icon(Icons.Default.Person, "Name Icon") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            colors = textFieldColors,
            enabled = registrationState !is Resource.Loading
        )

        OutlinedTextField(
            value = emailInput,
            onValueChange = { emailInput = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, "Email Icon") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = textFieldColors,
            enabled = registrationState !is Resource.Loading
        )

        OutlinedTextField(
            value = passwordInput,
            onValueChange = { passwordInput = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, "Password Icon") },
            trailingIcon = {
                val imageResId = if (isPasswordVisible)
                    R.drawable.ic_visibility
                else R.drawable.ic_visibility_off
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(painter = painterResource(id = imageResId), "Toggle password visibility")
                }
            },
            singleLine = true,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = textFieldColors,
            enabled = registrationState !is Resource.Loading
        )

        OutlinedTextField(
            value = confirmPasswordInput,
            onValueChange = { confirmPasswordInput = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            label = { Text("Confirm Password") },
            leadingIcon = { Icon(Icons.Default.Lock, "Confirm Password Icon") },
            trailingIcon = {
                val imageResId = if (isConfirmPasswordVisible)
                    R.drawable.ic_visibility
                else R.drawable.ic_visibility_off
                IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                    Icon(painter = painterResource(id = imageResId), "Toggle confirm password visibility")
                }
            },
            singleLine = true,
            visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = textFieldColors,
            enabled = registrationState !is Resource.Loading
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (registrationState is Resource.Loading) {
            CircularProgressIndicator(color = AppOnPrimaryColor)
        } else {
            NextButton(
                onClick = {
                    if (nameInput.isBlank() || emailInput.isBlank() || passwordInput.isBlank() || confirmPasswordInput.isBlank()) {
                        android.widget.Toast.makeText(context, "All fields are required.", android.widget.Toast.LENGTH_SHORT).show()
                        return@NextButton
                    }
                    if (passwordInput != confirmPasswordInput) {
                        android.widget.Toast.makeText(context, "Passwords do not match.", android.widget.Toast.LENGTH_SHORT).show()
                        return@NextButton
                    }
                    authViewModel.registerUser(
                        RegisterRequest(
                            name = nameInput.trim(),
                            email = emailInput.trim(),
                            password = passwordInput,
                            passwordConfirmation = confirmPasswordInput
                        )
                    )
                }
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        TextButton(onClick = {
            navigator.navigate(LogInScreenDestination) { 
               popUpTo(SignUpScreenDestination.route) { inclusive = true }
            }
        }) {
            Text("Already have an account? Log In", color = AppOnPrimaryColor, modifier = Modifier.padding(bottom = 16.dp))
        }
    }
}

@Preview(device = Devices.PIXEL)
@Composable
fun SignUpScreenPrev() {
    NeatFlixTheme {
        Column(
            modifier = Modifier.fillMaxSize().background(Color(0xFF180E36)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Sign Up Screen Preview (Actual requires Hilt/Nav)", color = Color.White, textAlign = TextAlign.Center)
        }
    }
}
