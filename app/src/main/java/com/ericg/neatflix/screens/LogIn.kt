package com.ericg.neatflix.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
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
import com.ericg.neatflix.model.LoginRequest
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
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun LogInScreen(
    navigator: DestinationsNavigator, 
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val loginState by authViewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is Resource.Success -> {
                if (state.data?.token != null) {
                    authViewModel.resetLoginState() 
                    navigator.navigate(HomeDestination) { 
                        popUpTo(LogInScreenDestination.route) { inclusive = true }
                    }
                }
            }
            is Resource.Error -> {
                android.widget.Toast.makeText(context, state.statusMessage ?: "Login Failed", android.widget.Toast.LENGTH_LONG).show()
                authViewModel.resetLoginState() 
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
            Text(
                text = "Welcome back",
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
            value = emailInput,
            onValueChange = { emailInput = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, "Email Icon") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = textFieldColors,
            enabled = loginState !is Resource.Loading 
        )

        OutlinedTextField(
            value = passwordInput,
            onValueChange = { passwordInput = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
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
            enabled = loginState !is Resource.Loading 
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (loginState is Resource.Loading) {
            CircularProgressIndicator(color = AppOnPrimaryColor)
        } else {
            NextButton(
                onClick = {
                    if (emailInput.isNotBlank() && passwordInput.isNotBlank()) {
                        authViewModel.loginUser(
                            LoginRequest(
                                email = emailInput.trim(),
                                password = passwordInput
                            )
                        )
                    } else {
                        android.widget.Toast.makeText(context, "Email and password cannot be empty.", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        
        TextButton(onClick = {
            navigator.navigate(SignUpScreenDestination)
        }) {
            Text("Don't have an account? Sign Up", color = AppOnPrimaryColor, modifier = Modifier.padding(bottom = 16.dp))
        }
    }
}

@Preview(device = Devices.DEFAULT)
@Composable
fun LogInScreenPrevWithTheme() { 
    NeatFlixTheme { 
        Column(
            modifier = Modifier.fillMaxSize().background(Color(0xFF180E36)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
            ){
             Text("Login Screen Preview (Actual requires Hilt/Nav)", color = Color.White, textAlign = TextAlign.Center)
        }
    }
}