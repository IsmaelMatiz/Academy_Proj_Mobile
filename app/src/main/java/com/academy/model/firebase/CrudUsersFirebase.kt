package com.academy.model.firebase

import android.util.Log
import com.academy.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CrudUsersFirebase {
    private val auth = FirebaseAuth.getInstance()

    suspend fun registerUser(email: String, password: String): FirebaseResult {
        return try {
            // Utiliza `await` directamente en la llamada a Firebase para esperar a que complete
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()

            // Si llegamos aquí, la creación de usuario fue exitosa
            val resultMessage = "Usuario creado exitosamente"
            Log.i("FIrebaseIsm","La app devuelve: ${resultMessage}")
            FirebaseResult(true, resultMessage)
        } catch (e: Exception) {
            // Si ocurre una excepción, significa que el registro falló
            val resultMessage = e.message ?: "Error desconocido"
            FirebaseResult(false, resultMessage)
        }
    }

    suspend fun loginUser(email: String, password: String): FirebaseResult {
        return suspendCoroutine { continuation ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Inicio de sesión exitoso
                        val resultMessage = "Inicio de sesión exitoso"
                        continuation.resume(FirebaseResult(true, resultMessage))
                    } else {
                        // Error al iniciar sesión
                        val resultMessage = task.exception?.message ?: ""
                        continuation.resume(FirebaseResult(false, resultMessage))
                    }
                }
        }
    }

    fun logoutUser() {
        auth.signOut()
    }

    /**
     * CUIDADO: esta funcion elimina el usuario que este logueado en el momento
     */
    fun deleteUser(): FirebaseResult {
        val user = auth.currentUser
        var isSuccessfullyCompleted = false
        var resultMessage = ""

        user?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Usuario eliminado
                    isSuccessfullyCompleted = true
                    resultMessage = "Usuario eliminado exitosamente"
                } else {
                    // Error al eliminar usuario
                    isSuccessfullyCompleted = false
                    resultMessage = task.exception?.message?: ""
                }
            }
        return FirebaseResult(isSuccessfullyCompleted,resultMessage)
    }
}