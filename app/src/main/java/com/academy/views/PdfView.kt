package com.academy.views

import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import java.net.URLEncoder

@Composable
fun CronogramaPDF() {
    // setting the link of the pdf into a variable
    val url = "https://firebasestorage.googleapis.com/v0/b/academy-it-2024.appspot.com/o/Calendario-academico-ing-software.pdf?alt=media&token=70734519-bb45-4472-9c2d-f8cb14996744"
val encodedUrl = URLEncoder.encode(url, "UTF-8")

    Surface(
        modifier = Modifier.fillMaxSize()
            .padding(top = 30.dp, bottom = 25.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        PDFWebView("https://docs.google.com/viewer?url=$encodedUrl ")
    }

}

// creating a composable function for opening the url into a WebView, so that the internal browser can load the pdf
@Composable
fun PDFWebView(pdfUrl: String) {
    AndroidView(factory = { context ->
        WebView(context).apply {
            settings.javaScriptEnabled = true
            loadUrl(pdfUrl)
        }
    },
    modifier = Modifier.fillMaxSize())
}