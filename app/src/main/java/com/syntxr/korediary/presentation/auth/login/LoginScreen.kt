package com.syntxr.korediary.presentation.auth.login

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.syntxr.korediary.presentation.destinations.HomeScreenDestination
import com.syntxr.korediary.presentation.destinations.LoginScreenDestination
import com.syntxr.korediary.presentation.destinations.RegisterScreenDestination
import com.syntxr.korediary.utils.isValidEmail
import com.syntxr.korediary.utils.meetsRequirements

@Destination
@Composable
fun LoginScreen(
    navigator: DestinationsNavigator,
    viewModel: LoginViewModel = hiltViewModel(),
) {

    val snackBarHostState = SnackbarHostState() // state snackbar

    val email = viewModel.email.collectAsState(initial = "") // mendapatkan value email view model
    val password = viewModel.password.collectAsState("") // value password dari viewmodel

    var showPassword by remember { mutableStateOf(false) } // kondisi visible password
    var isPasswordError by remember { mutableStateOf(false) } // apakah password error ?
    var isEmailError by remember { mutableStateOf(false) } // apakah email error ?
    var passwordErrorMsg by remember { mutableStateOf("") } // text yang ditampilkan ketika passworderror

    val loginState = viewModel.loginState.collectAsStateWithLifecycle()
    val context = LocalContext.current // context

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState) // snackbar
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))
            Text(
                text = "Login",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )

//            email
            Spacer(modifier = Modifier.height(32.dp))
            TextField( // input text view
                value = email.value, // set value
                onValueChange = { value -> // ketika value berubah, mengembalikan string
                    isEmailError = value.isValidEmail()
                    viewModel.onEmailChange(value)
                },
                label = { // label input text
                    Text(text = "Email")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                // agar keyboard menyesuaikan dengan input text email
                isError = isEmailError, // set error
                supportingText = { // menampilkan text ketika error
                    if (isEmailError) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Email address is not valid",
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                },
                trailingIcon = { // ikon di belakang atau sebelah kanan
                    Icon(
                        imageVector = Icons.Rounded.Email,
                        contentDescription = "email trail icon"
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = CircleShape
            )

            // password
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = password.value,
                onValueChange = { value ->
                    if (value.length >= 8 && value.isNotEmpty()) {
                        isPasswordError = value.meetsRequirements
                        if (!isPasswordError)
                            passwordErrorMsg = "Use letter, number, and unique character"
                    } else {
                        isPasswordError = true
                        passwordErrorMsg = "Password must have at least 8 character"
                    }
                    viewModel.onPasswordChange(value)
                },
                label = {
                    Text(text = "Password")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                // untuk melihat password
                isError = isPasswordError,
                supportingText = {
                    if (isPasswordError) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = passwordErrorMsg,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                trailingIcon = {
                    val icon = if (showPassword)
                        Icons.Rounded.LockOpen
                    else Icons.Rounded.Lock

                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = icon,
                            contentDescription = "Visibility"
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = CircleShape
            )

            Spacer(modifier = Modifier.height(16.dp))


            Button(
                onClick = {
                    if (email.value.isNotEmpty() && password.value.isNotEmpty()) {
                        // kalau email dan password tidak kosong
                        viewModel.login() // login
                        viewModel.onEmailChange("") // set email menjadi kosong
                        viewModel.onPasswordChange("")
                    }else{ // kalau kosong
                        Toast.makeText(context, "fill the field first", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !loginState.value.isLoading(), // bisa di klik kalau lagi tidak loading
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (loginState.value.isLoading())
                    CircularProgressIndicator()
                else
                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.bodyLarge
                    )
            }

            loginState.value.DisplayResult(
                onSuccess = {
                    navigator.navigate(HomeScreenDestination) {
                        popUpTo(LoginScreenDestination.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onError = { message ->
                    Toast.makeText(LocalContext.current, message, Toast.LENGTH_SHORT).show()
                },
                onLoading = {}
            )


            Row(
                modifier = Modifier.wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "New Here (｡･∀･)ﾉﾞ ?")
                TextButton(onClick = {
                    // berpindah ke register
                    navigator.navigate(RegisterScreenDestination) {
                        popUpTo(LoginScreenDestination.route) {
                            inclusive = true // tidak bisa kembali ke screen ini, jika menekan back akan menutup aplikasi
                        }
                        launchSingleTop = true
                    }
                }) {
                    Text(text = "Sign Up")
                }
            }

        }
    }
}

@Preview
@Composable
fun LoginPrev() {
//    LoginScreen (
//        signIn = {
//            email, password ->
//        },
//        goToRegister = {},
//        state = loginState
//    )
}