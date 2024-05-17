package com.syntxr.korediary.presentation.auth.register

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
import androidx.compose.material.icons.rounded.Person
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
import com.syntxr.korediary.presentation.destinations.LoginScreenDestination
import com.syntxr.korediary.presentation.destinations.RegisterScreenDestination
import com.syntxr.korediary.utils.isValidEmail
import com.syntxr.korediary.utils.meetsRequirements


@Destination
@Composable
fun RegisterScreen(
    navigator: DestinationsNavigator,
    viewModel: RegisterViewModel = hiltViewModel(),
) {

    val snackBarHostState = SnackbarHostState()
    val context = LocalContext.current

    val email = viewModel.email.collectAsState(initial = "")
    // email yang masih kosong untuk di set ke input ext
    val password = viewModel.password.collectAsState("")
    // password yang masih kosong
    val username = viewModel.username.collectAsState("")
    // username yang masih kosong

    var showPassword by remember { mutableStateOf(false) }
    // tampilkan password ?
    var isPasswordError by remember { mutableStateOf(false) }
    // apakah password error ?
    var isEmailError by remember { mutableStateOf(false) }
    var isNameError by remember {
        mutableStateOf(false)
    }
    // apakah email error ?
    var passwordErrorMsg by remember { mutableStateOf("") }
    // menampilkan text error untuk input password
    val registerState = viewModel.registerState.collectAsStateWithLifecycle()
    // state yang berisi api wrapper (responseState)

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 16.dp)
                .fillMaxSize(), // fill max size = sepenuh layar
            verticalArrangement = Arrangement.Center, // ke tengah secara vertical
            horizontalAlignment = Alignment.CenterHorizontally // ke tengah secaa horizontal
        ) {
            Spacer(modifier = Modifier.height(64.dp)) // jarak dengan tinggi
            Text(
                text = "Register",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )

//            email
            Spacer(modifier = Modifier.height(32.dp))
            TextField(
                value = email.value,
                onValueChange = { value ->
                    isEmailError = value.isValidEmail() // memeriksa apakah email valid ?
                    viewModel.onEmailChange(value) // menyimpan perubahan value email
                },
                label = {
                    Text(text = "Email")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = isEmailError,
                supportingText = {
                    if (isEmailError) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Email address is not valid",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                trailingIcon = {
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
                        isPasswordError = value.meetsRequirements // apakah password valid
                        if (!isPasswordError)
                            passwordErrorMsg = "Use letter, number, and unique character"
                        // menyimpan text error
                    } else {
                        isPasswordError = true // mengatur bahwa error ditampilkan
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

// username
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = username.value,
                onValueChange = { value ->
                    viewModel.onUsernameChange(value)
                },
                label = {
                    Text(text = "Username")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Person,
                        contentDescription = "email trail icon"
                    )
                },
                isError = isNameError,
                supportingText = {
                    if (isNameError) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Username is empty",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = CircleShape
            )

            Spacer(modifier = Modifier.height(16.dp))


            Button(
                onClick = {
                    if (email.value.isNotEmpty() && password.value.isNotEmpty() && username.value.isNotEmpty()) {
                        viewModel.register()
                } else if (username.value.isNotEmpty()){
                    isNameError = true
                    } else {
                        Toast.makeText(context, "Make sure you fill all field", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !registerState.value.isLoading(),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (registerState.value.isLoading())
                    CircularProgressIndicator()
                else
                    Text(
                        text = "Register",
                        style = MaterialTheme.typography.bodyLarge
                    )
            }

            registerState.value.DisplayResult(
                onSuccess = {
                    navigator.navigate(LoginScreenDestination) {
                        popUpTo(RegisterScreenDestination.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onError = { message->
                    Toast.makeText(LocalContext.current, message, Toast.LENGTH_SHORT).show()
                },
                onLoading = {}
            )

            Row(
                modifier = Modifier.wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Already Join (oﾟvﾟ)ノ ?")
                TextButton(onClick = {
                    navigator.navigate(LoginScreenDestination) {
                        popUpTo(RegisterScreenDestination.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }) {
                    Text(text = "Sign In")
                }
            }

        }
    }
}


@Preview
@Composable
fun registerPrev() {
//    RegisterScreen(
//        signUp = {
//            email, password, username ->
//        },
//        goToLogin = {},
//        state =
//    )
}