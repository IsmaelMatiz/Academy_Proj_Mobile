package com.academy.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson

@Composable
fun WeekSelectView(careerId:Int, bimesterNum:Int, goToLesson: (String) -> Unit){
    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        for (i in 1..8){
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 25.dp, bottom = 25.dp)
                    .clickable {
                        val map = hashMapOf(
                            "careerId" to careerId,
                            "bimesterNum" to bimesterNum,
                            "weekNum" to i
                        )

                        goToLesson(Gson().toJson(map))
                    },
                text = "Semena $i",
                fontSize = 35.sp,
                textAlign = TextAlign.Center
            )
            HorizontalDivider(color = Color.Black)
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 25.dp, bottom = 25.dp),
            text = "Examen Final",
            fontSize = 35.sp,
            textAlign = TextAlign.Center
        )
    }
}
