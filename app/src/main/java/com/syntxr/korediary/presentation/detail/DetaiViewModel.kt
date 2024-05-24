package com.syntxr.korediary.presentation.detail

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.smarttoolfactory.screenshot.ScreenshotState
import com.syntxr.korediary.BuildConfig
import com.syntxr.korediary.presentation.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class DetaiViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val titleHandle = savedStateHandle.navArgs<DetailScreenNavArgs>().title
    val valueHandle = savedStateHandle.navArgs<DetailScreenNavArgs>().ysiyg
    val moodHandle = savedStateHandle.navArgs<DetailScreenNavArgs>().mood
    val dateHandle = savedStateHandle.navArgs<DetailScreenNavArgs>().date

    fun shareImage(context: Context, bitmap: Bitmap): Uri {
        val tempFile = File.createTempFile("tempshareimg", ".jpeg")
        val bytes = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val bitmapData = bytes.toByteArray()
        val fileOutput = FileOutputStream(tempFile)
        fileOutput.write(bitmapData)
        fileOutput.flush()
        fileOutput.close()
        return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", tempFile)
    }

}