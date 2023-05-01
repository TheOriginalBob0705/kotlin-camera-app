package com.example.cameraapp.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.cameraapp.Routes
import com.example.cameraapp.login.models.LoginResult
import com.example.cameraapp.login.models.LoginViewModel
import com.example.cameraapp.ui.theme.Purple700

@Composable
fun LoginPage(navController: NavHostController, viewModel: LoginViewModel) {
    val usernameState = remember { mutableStateOf(TextFieldValue()) }
    val passwordState = remember { mutableStateOf(TextFieldValue()) }
    val loginResultState by viewModel.loginResult.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        ClickableText(
            text = AnnotatedString("Sign up"),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp),
            onClick = { navController.navigate(Routes.SignUp.route) },
            style = MaterialTheme.typography.body2.copy(
                fontSize = 25.sp,
                textDecoration = TextDecoration.Underline,
                color = Purple700,
                fontWeight = FontWeight.Bold
            )
        )
    }
    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Camera App",
            style = MaterialTheme.typography.h2,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        TextField(
            label = { Text(text = "Username") },
            value = usernameState.value,
            onValueChange = { usernameState.value = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        TextField(
            label = { Text(text = "Password") },
            value = passwordState.value,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            onValueChange = { passwordState.value = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { viewModel.login(usernameState.value.text, passwordState.value.text) },
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Login")
        }

        Spacer(Modifier.height(20.dp))

        loginResultState?.let { loginResult ->
            when (loginResult) {
                is LoginResult.Success ->
                    navController.navigate(Routes.CameraPreview.route)
                is LoginResult.Error ->
                    Text(text = loginResult.message, color = Color.Red)
            }
        }
    }
}
