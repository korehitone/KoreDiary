package com.syntxr.korediary.presentation.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.syntxr.korediary.data.kotpref.GlobalPreferences
import com.syntxr.korediary.data.kotpref.GlobalPreferences.AppTheme.DEFAULT_DARK
import com.syntxr.korediary.data.kotpref.GlobalPreferences.AppTheme.DEFAULT_LIGHT
import com.syntxr.korediary.data.kotpref.GlobalPreferences.AppTheme.MOUNTAIN_DARK
import com.syntxr.korediary.data.kotpref.GlobalPreferences.AppTheme.MOUNTAIN_LIGHT
import com.syntxr.korediary.data.kotpref.GlobalPreferences.AppTheme.SAKURA_DARK
import com.syntxr.korediary.data.kotpref.GlobalPreferences.AppTheme.SAKURA_LIGHT
import com.syntxr.korediary.data.kotpref.LocalUser
import com.syntxr.korediary.presentation.destinations.HomeScreenDestination
import com.syntxr.korediary.presentation.destinations.LoginScreenDestination
import com.syntxr.korediary.utils.GlobalState
import com.syntxr.korediary.utils.isValidEmail
import com.syntxr.korediary.utils.meetsRequirements

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun SettingsScreen(
    navigator: DestinationsNavigator,
    viewModel: SettingsViewModel = hiltViewModel(),
) {

    var expanded by remember { mutableStateOf(false) }
    // val untuk dropdown tema, apakah diperlihatkan atau tidak
    var selectedTheme by remember {
        mutableStateOf(GlobalState.theme)
        // tema yang terpilih, nilai default mengambil dari GlobalState
    }

    var name by remember { mutableStateOf(GlobalState.username) }
        //  username saat ini, nilai default dari GlobalState

    var email by remember { mutableStateOf(GlobalState.email) }
// email saat ini
    var password by remember {
        mutableStateOf(GlobalState.password) // password saat ini
    }

    var isPasswordError by remember { mutableStateOf(false) }
    var isEmailError by remember { mutableStateOf(false) }
    var passwordErrorMsg by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val themes = listOf(
        // list untuk ditampilkan di dropdown
        DEFAULT_DARK,
        DEFAULT_LIGHT,
        MOUNTAIN_DARK,
        MOUNTAIN_LIGHT,
        SAKURA_DARK,
        SAKURA_LIGHT
    )

    Scaffold { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val maxHeight = this.maxHeight
            val headerHeight = maxHeight / 5
            val bodyHeight = maxHeight * 5 / 6
            Image(
                painter = painterResource(id = selectedTheme.background),
                // background gambar sesuai dengan tema
                contentDescription = null,
                modifier = Modifier
                    .height(headerHeight)
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                contentScale = ContentScale.Crop
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(bodyHeight)
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape( // agar ujung komponen bulat
                    topStart = 24.dp,
                    topEnd = 24.dp,
                    bottomEnd = 0.dp,
                    bottomStart = 0.dp
                ),
                content = {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        OutlinedTextField( // input email
                            value = email,
                            onValueChange = {
                                isEmailError = it.isValidEmail()
                                email = it
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Email,
                                    contentDescription = "email trail icon"
                                )
                            },
                            singleLine = true,
                            isError = isEmailError,
                            supportingText = {
                                if (isEmailError) {
                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        text = "Email address is not valid",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }

                            }
                        )

                        OutlinedTextField( // input password
                            value = password,
                            onValueChange = {
                                if (it.length >= 8 && it.isNotEmpty()) {
                                    isPasswordError = it.meetsRequirements
                                    if (!isPasswordError)
                                        passwordErrorMsg = "Use letter, number, and unique character"
                                } else {
                                    isPasswordError = true
                                    passwordErrorMsg = "Password must have at least 8 character"
                                }
                                password = it
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
                            }

                        )

                        OutlinedTextField( // input username
                            value = name,
                            onValueChange = {
                                name = it
                            },
                            trailingIcon = {
                                IconButton(onClick = {
                                    if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                                        // periksa apakah input text kosong ?
                                        viewModel.update(name, email, password)
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Rounded.Save,
                                        contentDescription = null,
                                    )
                                }
                            },
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Spacer(modifier = Modifier.height(24.dp))

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.tertiary,
                            thickness = 1.6.dp
                        ) // untuk membuat garis
                        Spacer(modifier = Modifier.height(16.dp))


                        Row(
                            modifier = Modifier
                                .align(Alignment.Start),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Settings,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Settings",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        Column(
                            modifier = Modifier
                                .width(IntrinsicSize.Min)
                                .align(Alignment.Start)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ColorLens,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Theme",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            ExposedDropdownMenuBox( // dropdown
                                expanded = expanded, // memanggil boolean, apakah dropdown di tampilkan
                                onExpandedChange = { // ketika kondisi berubah, mengembalikan boolean untuk expanded
                                    expanded = !expanded
                                },
                            ) {
                                OutlinedTextField(
                                    readOnly = true,
                                    value = selectedTheme.id,
                                    onValueChange = {},
                                    trailingIcon = {
                                        Icon(
                                            imageVector = if (!expanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                                            contentDescription = null,
                                        )
                                    },
                                    modifier = Modifier.menuAnchor()
                                // menu anchor digunakan untuk memberi tahu dropdown menu bahwa komponen ini digunakan sebagai jangkar dari menu
                                )

                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }) {
                                    themes.forEach { theme -> // setiap value dari list tema  yang dibuat
                                        DropdownMenuItem( // dropdown item
                                            text = { Text(text = theme.id) }, // nama tema
                                            onClick = { // ketika di klik
                                                expanded = false
                                                GlobalState.theme = theme // tema terpilih disimpan
                                                GlobalPreferences.theme = theme
                                                selectedTheme = theme
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Button( // button logout
                                onClick = {
                                    navigator.navigate(LoginScreenDestination) {
                                        popUpTo(HomeScreenDestination.route) {
                                            inclusive = true // mengarahkan user ke login, dan tidak bisa kembali
                                        }
                                        launchSingleTop = true
                                    }
                                    LocalUser.clear() // menghapus data user yang disimpan
                                }
                            ) {
                                Text(text = "Log Out")
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ExitToApp,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                    }
                }
            )

            IconButton(onClick = { navigator.navigateUp() }) { // tombol untuk kembali ke screen sebelumnya
                Icon(
                    imageVector = Icons.Rounded.ArrowBackIosNew,
                    contentDescription = null
                )
            }
        }
    }
}

@Preview
@Composable
fun SettingPrev() {
//    SettingsScreen()
}