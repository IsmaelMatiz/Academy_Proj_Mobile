package com.academy.modelViews

import android.app.Application
import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.academy.Constants
import com.academy.UserTypes
import com.academy.model.apiInteractions.DTOs.Career
import com.academy.model.apiInteractions.DTOs.Student
import com.academy.model.apiInteractions.RetrofitClient
import com.academy.model.apiInteractions.webServices.CareersService
import com.academy.model.apiInteractions.webServices.StudentService
import com.academy.model.firebase.CrudUsersFirebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterVM(application: Application) : AndroidViewModel(application) {
    private val careersService = RetrofitClient.retrofit.create(CareersService::class.java)
    private val studentService = RetrofitClient.retrofit.create(StudentService::class.java)
    private val fireUserCrud = CrudUsersFirebase()

    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var careers by mutableStateOf<List<Career>>(emptyList())
    var choosenCareer by mutableStateOf(Career())

    // Estado para el loading y el error
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf("")

    var isPassVisible by mutableStateOf(false)

    // Función para manejar el registro
    suspend fun onRegisterClick(): Boolean {
        var isSuccessfullyRegisterd = false
        if (validateInput()) {
            isLoading = true
            // Aquí puedes iniciar una corrutina para llamar a un API o Firebase
            viewModelScope.async {
                try {

                    var resultRegisterFire = fireUserCrud.registerUser(email,password)
                    var resultRegisterApi: Response<String>? = null

                    Log.i(Constants.LOG_TAG.constVal,"Firebase devuelve: ${resultRegisterFire.isSuccessResult}")
                    if (resultRegisterFire.isSuccessResult){
                        Log.i(Constants.LOG_TAG.constVal,"Empieza el registro en la API")
                        resultRegisterApi = studentService.createStudent(
                            Student(
                                id = null,
                                email = email,
                                fullName = name,
                                userType = UserTypes.STUDENT.name,
                                choosenCareer = choosenCareer,
                                profilePic = ""
                            )
                        )

                        Log.i(Constants.LOG_TAG.constVal,"Termina el registro en la API con: ${resultRegisterApi.isSuccessful}\n" +
                                "la api dice ${resultRegisterApi.body()}")
                    }else{
                        errorMessage = resultRegisterFire.resultMessage
                    }

                    if(resultRegisterFire.isSuccessResult && resultRegisterApi?.isSuccessful?:false){
                        isSuccessfullyRegisterd = true
                    }else{
                        isSuccessfullyRegisterd = false
                    }

                    isLoading = false

                } catch (e: Exception) {
                    Log.e(Constants.LOG_TAG.constVal, "Hubo en erro en el registro:${e.message}\n" +
                            "stack: ${e.cause}\n" +
                            "moreInfo: ${e.localizedMessage}")
                    isLoading = false
                    errorMessage = "Error al registrar el usuario"
                    isSuccessfullyRegisterd = false
                }
            }.await()
        } else {
            isSuccessfullyRegisterd = false
        }

        return isSuccessfullyRegisterd
    }

    // Validar el formulario
    private fun validateInput(): Boolean {
        return when {
            name.isBlank() -> {
                errorMessage = "El nombre no puede estar vacío"
                false
            }
            email.isBlank() -> {
                errorMessage = "El correo electrónico no puede estar vacío"
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                errorMessage = "Ingresa un formato de correo correcto"
                false
            }

            password.isBlank() -> {
                errorMessage = "La contraseña no puede estar vacía"
                false
            }
            password != confirmPassword -> {
                errorMessage = "Las contraseñas no coinciden"
                false
            }
            password.count() < 6 ->{
                errorMessage = "La Contraseña debe ser de minimo 6 caracteres"
                false
            }
            choosenCareer.careerId == 0 -> {
                errorMessage = "Debes elegir una carrera"
                false
            }
            else -> true
        }
    }

    fun fetchCareers(){
        val call = careersService.getAllCareers()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
                if (response.isSuccessful) {
                    // Aquí manejas el body directamente como ResponseBody
                    val responseBody = response.body()
                    responseBody?.let {
                        // Procesa el contenido de la respuesta aquí, por ejemplo, lo puedes convertir a un string
                        val responseString = it.string()
                        Log.d("API Response", responseString)

                        val careerListType = object : TypeToken<List<Career>>() {}.type
                        val careersResult = Gson().fromJson<List<Career>>(responseString, careerListType)

                        careersResult.forEach {
                            Log.d("Lista de carreras", "itemLista: ${it.careerName}")
                        }
                        careers = careersResult
                    }
                } else {
                    Log.e("API Error", "Error en la solicitud: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("API Failure", "Error al hacer la solicitud: ${t.message}")
            }
        })
    }

    fun onPasswordVisibleSelected() { isPassVisible = !isPassVisible }

}