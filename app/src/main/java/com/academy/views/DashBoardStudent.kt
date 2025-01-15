package com.academy.views

import android.graphics.PointF
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardElevation
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.academy.Constants
import com.academy.R
import com.academy.model.apiInteractions.DTOs.ProgressStudent
import com.google.gson.Gson

@Composable
//@Preview(showSystemUi = true)
fun StudentDashView(studentProgressInfo: ProgressStudent?,
                    navToCronograma: () -> Unit,
                    navToWeekSelector: (String) -> Unit
) {
    var currentBimester = studentProgressInfo?.currentBimester?: 0
    Column {
        Column(modifier = Modifier
            .fillMaxSize()
            .weight(0.09f, true)
            .padding(bottom = 10.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Bienvenido ${studentProgressInfo?.student?.fullName?:""}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold)
        }
        HorizontalDivider()
        Column(
            modifier = Modifier
                .weight(0.9f, true)
                .verticalScroll(rememberScrollState())
                .padding(5.dp)
            ,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            cardMenu(picRef = R.drawable.cronograma_pic, contDescription = "Bimester pic",
                txtCard = "Cronograma", onClick = {navToCronograma()})

            if (currentBimester != 0) {
                for (i in 1..currentBimester){
                    cardMenu(picRef = R.drawable.bimesterpic, contDescription = "Bimester pic",
                        txtCard = "Bimestre $i", onClick = {
                            val map = hashMapOf(
                                "careerId" to studentProgressInfo?.student?.choosenCareer?.careerId,
                                "bimesterNum" to i
                            )

                            navToWeekSelector(Gson().toJson(map))
                        })
                }
            }else{
                Log.e(Constants.LOG_TAG.toString(),"StudentDashView: ProgressStudent esta vacio")
                Text(text = "Error obteniendo los bimestres, intente de nuevo mas tarde")
            }
        }
    }
}

@Composable
fun cardMenu(@DrawableRes picRef: Int, contDescription: String, txtCard: String,onClick: ()-> Unit) {
    Card(
        modifier = Modifier
            .padding(15.dp)
            .width(400.dp) // Ajusta el tamaño de la tarjeta
            .wrapContentHeight()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp) // Bordes redondeados
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Imagen con bordes superiores redondeados
            AsyncImage(
                model = picRef,
                contentDescription = contDescription,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)), // Redondear bordes superiores
                contentScale = ContentScale.Crop // Evita deformaciones
            )
            // Texto debajo de la imagen
            Text(
                text = txtCard,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}



