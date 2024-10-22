package com.academy.views

import android.content.Context
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.academy.UserTypes
import com.academy.model.apiInteractions.DTOs.Career
import com.academy.modelViews.RegisterVM

@Composable
fun RegisterView(
    viewModel: RegisterVM,
    context: Context
) {

    val fullName = viewModel.name
    val email = viewModel.email
    val password = viewModel.password
    val confirmPass = viewModel.confirmPassword

    viewModel.fetchCareers()
    var careers = viewModel.careers

    val isLoading: Boolean = viewModel.isLoading
    val isPasswordVisible: Boolean = viewModel.isPassVisible

    var shouldRegister by remember { mutableStateOf(false) }
    // Este efecto será disparado cuando `shouldRegister` cambie a `true`
    LaunchedEffect(shouldRegister) {
        if (shouldRegister) {
            val wasAuthSuccessfullyDone = viewModel.onRegisterClick()

            Log.i("ViewRegister", "Resultado del registro: $wasAuthSuccessfullyDone")
            if (wasAuthSuccessfullyDone) {
                Toast.makeText(
                    context, "Usuario registrado exitosamente",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    context, "Error al registrar usuario: ${viewModel.errorMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
            shouldRegister = false // Reiniciar el estado
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /*Image(painter = painterResource(id = R.drawable.book_store),
                contentDescription = "appLogo",
                modifier = Modifier.size(300.dp))*/
            Text(text = "Register")
            Spacer(modifier = Modifier.padding(16.dp))

            NormalTxtField(
                value = fullName,
                onTextFieldChanged = { viewModel.name = it },
                placeHolderTxt = "Ingresa tu nombre completo")
            Spacer(modifier = Modifier.padding(16.dp))

            EmailField(email, { viewModel.email = it })
            Spacer(modifier = Modifier.padding(16.dp))

            PasswordField(
                password,
                { viewModel.password = it },
                isPasswordVisible,
                { viewModel.onPasswordVisibleSelected() })
            Spacer(modifier = Modifier.padding(16.dp))

            PasswordField(
                confirmPass,
                { viewModel.confirmPassword = it },
                isPasswordVisible,
                { viewModel.onPasswordVisibleSelected() })
            Spacer(modifier = Modifier.padding(16.dp))

            DropdownCareers(listToIterate = careers) {
                viewModel.choosenCareer = it
            }
            Spacer(modifier = Modifier.padding(16.dp))

            RegisterButton() {
                shouldRegister = true
            }
        }
    }
}

@Composable
fun NormalTxtField(value: String, onTextFieldChanged: (String) -> Unit, placeHolderTxt: String) {
    Row(Modifier.padding(15.dp)) {
        TextField(
            value = value,
            onValueChange = { onTextFieldChanged(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = placeHolderTxt) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = true,
            maxLines = 1,
            colors = TextFieldDefaults.colors(
                cursorColor = Color(0xFF636262),
                unfocusedTextColor = Color(0xFF636262),
                unfocusedContainerColor = Color.White
            )
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownCareers(
    listToIterate: List<Career>,
    onOptionSelected: (Career) -> Unit // Callback para devolver la opción seleccionada
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(Career()) }

    Row(modifier = Modifier.padding(15.dp)) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedOption.careerName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Selecciona una opción") },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                if (listToIterate.isNotEmpty()) {
                    // Mostrar DropdownMenu
                    listToIterate.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion.careerName) },
                            onClick = {
                                selectedOption = opcion // Cambiar la opción seleccionada
                                expanded = false
                                onOptionSelected(opcion) // Llamar a la función callback pasando la opción seleccionada
                            }
                        )
                    }
                }else{
                    DropdownMenuItem(
                        text = { Text("No hay carreras, intenta mas tarde") },
                        onClick = {
                            selectedOption = Career()
                            expanded = false
                            onOptionSelected(Career()) // Llamar a la función callback pasando la opción seleccionada
                        }
                    )
                }
            }
        }
    }

}

@Composable
fun RegisterButton(onLoginSelected: () -> Unit) {
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
            )
        ) {
            Text(text = "Registrarse")
        }
    }
}
