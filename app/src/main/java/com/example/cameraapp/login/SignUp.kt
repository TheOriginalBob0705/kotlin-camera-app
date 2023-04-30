package com.example.cameraapp.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cameraapp.Routes
import com.example.cameraapp.login.components.CustomTopAppBar
import com.example.cameraapp.login.models.SignUpResult
import com.example.cameraapp.login.models.SignUpViewModel

@Composable
fun SignUp(navController : NavHostController, viewModel : SignUpViewModel) {
    Box(modifier = Modifier.fillMaxSize()) {
        ScaffoldWithTopBar(navController, viewModel)
    }
}

@Composable
fun ScaffoldWithTopBar(navController : NavHostController, viewModel: SignUpViewModel) {

    val usernameState = remember { mutableStateOf(TextFieldValue()) }
    val passwordState = remember { mutableStateOf(TextFieldValue()) }
    val confirmPasswordState = remember { mutableStateOf(TextFieldValue()) }
    val signUpResultState by viewModel.signUpResult.collectAsState()

    Scaffold(
        topBar = {
            CustomTopAppBar(navController, "Sign up", true)
        },
        content = { padding ->
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Spacer(modifier = Modifier.height(20.dp))
                TextField(
                    label = { Text(text = "Username") },
                    value = usernameState.value,
                    onValueChange = { usernameState.value = it }
                )

                Spacer(modifier = Modifier.height(20.dp))
                TextField(
                    label = { Text(text = "Password") },
                    value = passwordState.value,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    onValueChange = { passwordState.value = it }
                )

                Spacer(modifier = Modifier.height(20.dp))
                TextField(
                    label = { Text(text = "Confirm Password") },
                    value = confirmPasswordState.value,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    onValueChange = { confirmPasswordState.value = it }
                )

                Spacer(modifier = Modifier.height(20.dp))
                Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                    Button(
                        onClick = {
                            viewModel.signUp(
                                usernameState.value.text,
                                passwordState.value.text,
                                confirmPasswordState.value.text
                            )
                        },
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(text = "Sign Up")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (signUpResultState != null) {
                    when (signUpResultState) {
                        is SignUpResult.Success -> {
                            SignUpDialog(onDismiss = { navController.navigate(Routes.Login.route) })
                        }
                        is SignUpResult.Error -> {
                            Text(
                                text = (signUpResultState as SignUpResult.Error).message,
                                color = Color.Red
                            )
                        }
                        else -> {
                            // Do nothing
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun SignUpDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Success!") },
        text = { Text("You have successfully signed up!") },
        confirmButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("OK")
            }
        }
    )
}
