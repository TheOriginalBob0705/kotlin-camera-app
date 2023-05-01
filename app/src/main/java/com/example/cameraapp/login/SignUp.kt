package com.example.cameraapp.login

import android.annotation.SuppressLint
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

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SignUp(navController: NavHostController, viewModel: SignUpViewModel) {
    Scaffold(
        topBar = { CustomTopAppBar(navController, "Sign up", true) }
    ) {
        SignUpContent(navController, viewModel)
    }
}

@Composable
fun SignUpContent(navController : NavHostController, viewModel: SignUpViewModel) {
    val usernameState = remember { mutableStateOf(TextFieldValue()) }
    val passwordState = remember { mutableStateOf(TextFieldValue()) }
    val confirmPasswordState = remember { mutableStateOf(TextFieldValue()) }
    val signUpResultState by viewModel.signUpResult.collectAsState()

    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            label = { Text("Username") },
            value = usernameState.value,
            onValueChange = { usernameState.value = it }
        )
        Spacer(modifier = Modifier.height(20.dp))
        PasswordTextField(
            label = "Password",
            passwordState = passwordState
        )
        Spacer(modifier = Modifier.height(20.dp))
        PasswordTextField(
            label = "Confirm Password",
            passwordState = confirmPasswordState
        )
        Spacer(modifier = Modifier.height(20.dp))
        SignUpButton(
            username = usernameState.value.text,
            password = passwordState.value.text,
            confirmPassword = confirmPasswordState.value.text,
            viewModel = viewModel
        )
        Spacer(modifier = Modifier.height(20.dp))
        signUpResultState?.let { result ->
            when (result) {
                is SignUpResult.Success -> SignUpDialog(onDismiss = { navController.navigate(Routes.Login.route) })
                is SignUpResult.Error -> {
                    Text(text = result.message, color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun PasswordTextField(label: String, passwordState: MutableState<TextFieldValue>) {
    TextField(
        label = { Text(label) },
        value = passwordState.value,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        onValueChange = { passwordState.value = it }
    )
}

@Composable
fun SignUpButton(
    username: String,
    password: String,
    confirmPassword: String,
    viewModel: SignUpViewModel
) {
    Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
        Button(
            onClick = { viewModel.signUp(username, password, confirmPassword) },
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(text = "Sign Up")
        }
    }
}

@Composable
fun SignUpDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Success!") },
        text = { Text("You have successfully signed up!") },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}
