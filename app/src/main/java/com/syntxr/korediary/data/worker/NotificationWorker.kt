package com.syntxr.korediary.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.syntxr.korediary.utils.KEY_TITLE_NOTIFY
import com.syntxr.korediary.utils.KEY_TXT_NOTIFY
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.karn.notify.Notify

@HiltWorker
class NotificationWorker @AssistedInject constructor( // untuk worker kita menggunakan @AssistedInject
    @Assisted private val appContext: Context, // karena worker menggunakan CoroutineWorker, maka butuh context
    @Assisted params: WorkerParameters, // data akan dikirim lewat sini
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result { // worker akan dikerjakan lewat function ini
        Notify // kita menggunakan library  notification yang di buat oleh Karn, kalau penasaran bisa dilihat di github
            .with(appContext)
            .content {
                title = inputData.getString(KEY_TITLE_NOTIFY).toString()
                // title yang kita dapat dari params melalui inputdata.getString()
                text = inputData.getString(KEY_TXT_NOTIFY).toString()
                // text yang kita dapat dari params melalui input data getString()
            }
            .show()
        return Result.success() // success
    }

}