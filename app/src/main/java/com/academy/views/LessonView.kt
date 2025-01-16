package com.academy.views

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.academy.R
import com.academy.modelViews.LessonVM
import kotlinx.coroutines.launch
import java.net.URLEncoder

@Composable
fun LessonView(careerId:Int, bimesterNum: Int, weekNum: Int, viewModel: LessonVM, context: Context){

    val lessons = viewModel.lessonsInfo
    val itWasSuccess = viewModel.itWasSuccess
    var choseenLeson by remember {
        mutableStateOf(0)
    }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    

    // Llama a fetchLessons solo una vez al entrar en la vista
    LaunchedEffect(careerId, bimesterNum, weekNum) {
        viewModel.fetchLessons(careerId, bimesterNum, weekNum)
    }

    // Maneja los cambios en itWasSuccess y muestra el Toast
    LaunchedEffect(itWasSuccess) {
        if (itWasSuccess) {
            Toast.makeText(context, "Clases obtenidas exitosamente", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "No se encontraron lecciones para esta semana, intenta más tarde", Toast.LENGTH_SHORT).show()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .background(
                        color = Color.Blue
                    )
                    .padding(top = 40.dp)
            ) {
                lessons?.forEach {
                    Row(
                        modifier = Modifier
                            .padding(15.dp)
                            .clickable {
                                choseenLeson = it.posWeek - 1
                            }
                    ) {
                        Text(text = it.lessonTitle,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.padding(10.dp))
                }
            }
        },
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    scope.launch {
                        drawerState.apply {
                            if (isClosed) open() else close()
                        }
                    }
                }, content = {
                    Icon(imageVector = Icons.Filled.List, contentDescription = null)
                })
            },
            content = {paddingValues ->

                paddingValues
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (lessons != null) {
                        if (lessons.isNotEmpty())
                            if (lessons[choseenLeson].contentType == "PDF") {
                                showPDFLesson(lessons[choseenLeson].linkToContent, lessons[choseenLeson].lessonTitle)
                            } else {
                                showVideoLesson(lessons[choseenLeson].linkToContent,
                                    lessons[choseenLeson].lessonTitle,
                                    context,
                                    lessons[choseenLeson].contentDescrip)
                            }
                    }
                }
            }
        )
    }
}

@Composable
fun showVideoLesson(url:String, titleLesson: String, context: Context,
                    description: String) {
    val exoPlayer = ExoPlayer.Builder(context).build()

    val mediaSource = remember (url){
        MediaItem.fromUri(url)
    }

    LaunchedEffect(mediaSource) {
        exoPlayer.setMediaItem(mediaSource)
        exoPlayer.prepare()
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top)
    {
        Column(modifier = Modifier
            .fillMaxSize()
            .weight(0.09f, true)
            .padding(bottom = 10.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = titleLesson,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold)
        }
        Column(
            modifier = Modifier.weight(0.8f)
        ) {
            AndroidView(factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                }
            },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
            )

            Text(text = description)
        }
    }
}

@Composable
fun showPDFLesson(url:String, titleLesson: String) {
    val encodedUrl = URLEncoder.encode(url, "UTF-8")
    Column(modifier = Modifier.fillMaxSize())
    {
        Column(modifier = Modifier
            .fillMaxSize()
            .weight(0.09f, true)
            .padding(bottom = 10.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = titleLesson,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold)
        }
        Surface(
            modifier = Modifier
                .weight(0.8f)
                .padding(bottom = 20.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            PDFWebView("https://docs.google.com/viewer?url=$encodedUrl ")
        }
    }
}
