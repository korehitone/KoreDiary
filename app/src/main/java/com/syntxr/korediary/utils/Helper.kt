package com.syntxr.korediary.utils


import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Patterns
import androidx.work.Data
import com.syntxr.korediary.data.source.remote.serializable.PostDto
import java.text.SimpleDateFormat
import java.util.Date

//fun String.isLongEnough() = length >= 8 && isNotEmpty()
//fun String.hasEnoughDigits() = count(Char::isDigit) > 0
fun String.isMixedCase() = any(Char::isLowerCase) && any(Char::isUpperCase)
fun String.hasSpecialChar() = any { it in "!.,+^" }

val requirements = listOf(String::isMixedCase, String::hasSpecialChar)
val String.meetsRequirements get() = requirements.all { check -> check(this) }

//fun String.isValidPassword(): Boolean {
//    val regex = "[A-Za-z0-9@!%*?&]{8,}".toRegex()
//    return this.matches(regex)
//}

fun String.isValidEmail() = !Patterns.EMAIL_ADDRESS.matcher(this).matches()
//    val emailRegex = "[a-zA-Z0-9._-]+@[a-z.]+\\.+[a-z]+".toRegex()
//    return this.matches(emailRegex)
//}

fun isInternetAvailable(context: Context): Boolean {
    val result: Boolean
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkCapabilities = connectivityManager.activeNetwork ?: return false
    val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
    result = when {
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
    return result
}

fun PostDto.toDataWorker() = Data.Builder()
    .putString(KEY_UUID, this.uuid)
    .putString(KEY_USER, this.userId)
    .putString(KEY_TITLE, this.title)
    .putString(KEY_VALUE, this.value)
    .putString(KEY_MOOD, this.mood)
    .putString(KEY_DATE, this.createdAt)
    .build()

@SuppressLint("SimpleDateFormat")
fun String.parseToDate() = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(this)  as Date

@SuppressLint("SimpleDateFormat")
fun Date.formatToString() = SimpleDateFormat("MMM dd yyyy, HH:mm:ss").format(this) as String

//fun ByteArray.toBitmap(): Bitmap {
//    return BitmapFactory.decodeByteArray(this, 0, this.size)
//}

@Suppress("DEPRECATION")
object Network {
    private const val NETWORK_STATUS_NOT_CONNECTED = 0
    private const val NETWORK_STATUS_WIFI = 1
    private const val NETWORK_STATUS_MOBILE = 2
    private const val TYPE_WIFI = 1
    private const val TYPE_MOBILE = 2
    private const val TYPE_NOT_CONNECTED = 0
    private fun connectivityStatus(context: Context): Int {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        if (null != activeNetwork) {
            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) return TYPE_WIFI
            if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) return TYPE_MOBILE
        }
        return TYPE_NOT_CONNECTED
    }


    private fun connectivityStatusString(context: Context): Int {
        val connection = connectivityStatus(context)
        var status = -1
        if (connection == TYPE_WIFI) status = NETWORK_STATUS_WIFI else if (connection == TYPE_MOBILE) status = NETWORK_STATUS_MOBILE else if (connection == TYPE_NOT_CONNECTED) status = NETWORK_STATUS_NOT_CONNECTED
        return status
    }

    fun checkConnectivity(context : Context):Boolean{
        val status = connectivityStatusString(context)
        return status == NETWORK_STATUS_WIFI || status == NETWORK_STATUS_MOBILE
    }
}