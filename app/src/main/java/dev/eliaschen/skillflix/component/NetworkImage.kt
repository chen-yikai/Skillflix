package dev.eliaschen.skillflix.component

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import dev.eliaschen.skillflix.viewmodel.host
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

@Composable
fun NetworkImage(
    url: String,
    modifier: Modifier = Modifier, content: @Composable (ImageBitmap) -> Unit
) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(url) {
        bitmap = null
        try {
            bitmap = withContext(Dispatchers.IO) {
                URL("$host$url").openConnection().getInputStream().let {
                    BitmapFactory.decodeStream(it)
                }
            }
        } catch (e: Exception) {
            Log.e("NetworkImage","Failed to get network image")
        }
    }

    bitmap?.let {
        content(it.asImageBitmap())
    }
}