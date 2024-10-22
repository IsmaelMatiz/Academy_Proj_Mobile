package com.academy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.academy.modelViews.LoginVM
import com.academy.modelViews.RegisterVM
import com.academy.ui.theme.AcademyTheme
import com.academy.views.LoginView
import com.academy.views.RegisterView
import com.academy.views.StudentDashView

class MainActivity : ComponentActivity() {
    private lateinit var navController : NavHostController
    private lateinit var loginVM : LoginVM
    private lateinit var registerVM : RegisterVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            navController = rememberNavController()
            loginVM = LoginVM(application)
            registerVM = RegisterVM(application)

            AcademyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController = navController,
                        startDestination = ConstantViews.LOGIN_VIEW.route)
                    {
                        composable(ConstantViews.LOGIN_VIEW.route){
                            LoginView(context = baseContext, viewModel = loginVM, navToRegister = {
                                navController.navigate(ConstantViews.REGISTER_VIEW.route)
                            }, navToDashBoard = {navController.
                            navigate(ConstantViews.DASHSTUDENT_VIEW.route + "/${it}")})
                        }
                        composable(ConstantViews.REGISTER_VIEW.route){
                            RegisterView(context = baseContext, viewModel = registerVM)
                        }
                        composable(ConstantViews.DASHSTUDENT_VIEW.route+ "/{studentName}"){
                            backStackEntry ->
                            val studenName = backStackEntry.arguments?.getString("studentName")
                            StudentDashView(studentName = studenName?:"")
                        }
                    }
                }
            }
        }
    }

}

enum class ConstantViews(val route: String){
    LOGIN_VIEW("LoginView"),
    REGISTER_VIEW("RegisterView"),
    DASHSTUDENT_VIEW("DashboardStudentView")
}
