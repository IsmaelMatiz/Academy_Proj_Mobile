package com.academy.modelViews

import android.app.Application
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.academy.Constants
import com.academy.model.apiInteractions.DTOs.ProgressStudent
import com.academy.model.apiInteractions.RetrofitClient
import com.academy.model.apiInteractions.webServices.StudentService
import com.academy.model.firebase.CrudUsersFirebase
import com.academy.model.firebase.FirebaseResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.async

class LoginVM(application: Application): AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
    private val studentService = RetrofitClient.retrofit.create(StudentService::class.java)
    private val crudUsers = CrudUsersFirebase()

    private val _email = MutableLiveData<String>()
    val email : LiveData<String> = _email

    private val _password = MutableLiveData<String>()
    val password : LiveData<String> = _password

    private val _loginEnabled = MutableLiveData<Boolean>()
    val loginEnabled : LiveData<Boolean> = _loginEnabled

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _isPasswordVisible = MutableLiveData<Boolean>(false)
    val isPasswordVisible : LiveData<Boolean> = _isPasswordVisible

    /**
     * Updates the internal state of the email and password based on user input,
     * and sets the login button enabled/disabled based on validity checks.
     *
     * @param email The entered email address.
     * @param password The entered password.
     */
    fun onLoginChanged(email: String, password: String) {
        _email.value = email
        _password.value = password
        _loginEnabled.value = /*isValidEmail(email) && */isValidPassword(password)
    }

    /**
     * Simple password length validation.
     *
     * @param password The password to validate.
     * @return True if the password is at least 6 characters long, false otherwise.
     */
    private fun isValidPassword(password: String): Boolean  = password.length > 3

    /**
     * Uses the Android Patterns class to validate the email format(regex under the hood).
     *
     * @param email The email address to validate.
     * @return True if the email format is valid, false otherwise.
     */
    private fun isValidEmail(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    /**
     * Toggles the visibility state of the password field between plain text and hidden characters.
     */
    fun onPasswordVisibleSelected() {_isPasswordVisible.value = !_isPasswordVisible.value!!}

    /**
     * Coroutine function that handles the entire login process.
     * It performs the following steps:
     *  1. Fetches and stores the app key (if successful).
     *  2. Fetches and stores the OAuth key (if successful).
     *  3. Fetches and stores the session key (if successful).
     *  4. Logs the number of errors encountered during the process.
     *  5. Navigates to the landing screen if no errors occurred, otherwise displays an error message.
     *
     * @param navToLanding A lambda function to navigate to the landing screen.
     * @return 0 on success, 1 or more on any errors encountered.
     */
    suspend fun onLoginSelected(navToLanding:(ProgressStudent?) -> Unit): Int {
        Log.i("Academy","Inicio el proceso de logueo")
        _isLoading.value = true
        var userProgresInfo: ProgressStudent? = null

        var errorsAlongTheProcess = 0

        // Launch separate coroutines for each auth step to potentially improve performance
        viewModelScope.async {
            errorsAlongTheProcess += authStep1(email = email.value.toString(), password = password.value.toString())
            if (errorsAlongTheProcess >= 1) {
                Log.i(Constants.LOG_TAG.constVal,"Errors along Auth process $errorsAlongTheProcess errors")
            }else{
                errorsAlongTheProcess += authStep2( onUserNameRetrieved = {userProgresInfo = it}, email = email.value.toString() )
            }
        }.await()

        Log.i("IsmInfo","Errors along Auth process $errorsAlongTheProcess errors")
        if (errorsAlongTheProcess == 0)
        {
            navToLanding(userProgresInfo)
            _isLoading.value = false
            return 0
        }
        else
        {
            _isLoading.value = false
            return 1
        }
    }

    /**
     * Authentication in Firebase
     */
    suspend fun authStep1(email: String, password: String): Int{
        var feedbackFirebase = FirebaseResult(false,"")
        var iserror = 0
        try {
            viewModelScope.async {
                feedbackFirebase = crudUsers.loginUser(email, password)

                if (!feedbackFirebase.isSuccessResult){
                    Log.e(Constants.LOG_TAG.constVal, "An error occur loggin in firebase says: ${feedbackFirebase.resultMessage}")
                    iserror = 1
                }
            }.await()
        }catch (e : Exception){
            Log.e(Constants.LOG_TAG.constVal, "An error occur along first auth step:\n$e")
            return 1 // Indicate failure
        }
        return iserror // Indicate success
    }

    /**
     * Get student info in the API
     */
    suspend fun authStep2(
        onUserNameRetrieved: (ProgressStudent?) -> Unit,
        email: String
    ): Int {
        return try {
            val response = studentService.getStudentByEmail(email)

            if (response.isSuccessful) {
                val responseBody = response.body()
                responseBody?.let {
                    // Procesa el contenido de la respuesta aquí, por ejemplo, lo puedes convertir a un string
                    val responseString = it.string()
                    Log.d("API Response", responseString)

                    val progressType = object : TypeToken<ProgressStudent>() {}.type
                    val studentInfoResult = Gson().fromJson<ProgressStudent>(responseString, progressType)

                    onUserNameRetrieved(studentInfoResult)
                }
                0 // Success
            } else {
                Log.e("API Error", "Error en la solicitud: ${response.errorBody()?.string()}")
                1 // Error
            }
        } catch (e: Exception) {
            Log.e(Constants.LOG_TAG.constVal, "An error occurred during the second auth step:\n$e")
            1 // Error
        }
    }
}