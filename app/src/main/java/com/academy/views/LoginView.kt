package com.academy.views

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.academy.R
import com.academy.model.apiInteractions.DTOs.ProgressStudent
import com.academy.modelViews.LoginVM
import kotlinx.coroutines.launch


@Composable
fun LoginView(viewModel: LoginVM, navToRegister: () -> Unit, context: Context, navToDashBoard: (ProgressStudent?) -> Unit){

    val email : String by viewModel.email.observeAsState(initial = "")
    val password : String by viewModel.password.observeAsState(initial = "")
    val loginEnable: Boolean by viewModel.loginEnabled.observeAsState(initial = false)
    val coroutineScope = rememberCoroutineScope()

    val isLoading: Boolean by viewModel.isLoading.observeAsState(false)
    val isPasswordVisible: Boolean by viewModel.isPasswordVisible.observeAsState(false)

    if(isLoading){
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    }else{
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /*Image(painter = painterResource(id = R.drawable.book_store),
                contentDescription = "appLogo",
                modifier = Modifier.size(300.dp))}*/
            Text(text = "Login")
            Spacer(modifier = Modifier.padding(16.dp))

            EmailField(email,{viewModel.onLoginChanged(it, password)})
            Spacer(modifier = Modifier.padding(16.dp))

            PasswordField(password, {viewModel.onLoginChanged(email, it)}
                ,isPasswordVisible,{viewModel.onPasswordVisibleSelected()})
            Spacer(modifier = Modifier.padding(16.dp))

            LoginButton(loginEnable = loginEnable) {
                coroutineScope.launch {
                    var wasAuthSuccessfullyDone = viewModel.onLoginSelected({navToDashBoard(it)})

                    if (wasAuthSuccessfullyDone == 1)
                        Toast.makeText(context,"authenticacion wrong" +
                                " check the credentials or try a again later",
                            Toast.LENGTH_LONG).show()
                }
            }
            Spacer(modifier = Modifier.padding(20.dp))
            Text(
                text = "Registrate aquí",
                color = Color.Blue,
                modifier = Modifier.clickable {
                    navToRegister()
                }
            )
        }
    }
}

@Composable
fun EmailField(email: String, onTextFieldChanged:(String) -> Unit) {
    Row(Modifier.padding(15.dp)) {
        TextField(
            value = email
            , onValueChange = {onTextFieldChanged(it)}
            , modifier = Modifier.fillMaxWidth()
            , placeholder = { Text(text = "Email") }
            , keyboardOptions = KeyboardOptions(keyboardType =  KeyboardType.Email)
            , singleLine = true
            , maxLines = 1
            , colors = TextFieldDefaults.colors(
                cursorColor =  Color(0xFF636262),
                unfocusedTextColor = Color(0xFF636262),
                unfocusedContainerColor = Color.White)
        )
    }
}

@Composable
fun LoginButton(loginEnable: Boolean, onLoginSelected: () -> Unit) {
    Row(Modifier.padding(15.dp)) {
        Button(
            onClick = { onLoginSelected() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Magenta,
                disabledContainerColor = Color.Gray,
                contentColor = Color.White,
                disabledContentColor = Color.White
            ), enabled = loginEnable
        ) {
            Text(text = "Iniciar sesión")
        }
    }
}

@Composable
public fun PasswordField(
    value: String,
    onTextFieldChanged: (String) ->Unit,
    isPasswordVisible: Boolean,
    onPassVisiblePressd: () -> Unit) {

    Row(Modifier.padding(15.dp)) {

        TextField(
            value = value,
            onValueChange = { onTextFieldChanged(it) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("Password") },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = TextFieldDefaults.colors(
                cursorColor = Color.Magenta,
                unfocusedTextColor = Color.LightGray,
                unfocusedContainerColor = Color.White
            ),
            trailingIcon = {
                val image = if (isPasswordVisible)
                    R.drawable.baseline_visibility_off_24
                else R.drawable.baseline_visibility_24

                val description = if (isPasswordVisible) "Hide password" else "Show password"

                IconButton(onClick = { onPassVisiblePressd() }) {
                    Icon(painter = painterResource(id = image), contentDescription = description)
                }
            }
        )
    }
}