package com.syntxr.korediary.data.repository

import android.content.Context
import android.widget.Toast
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.rmaprojects.apirequeststate.ResponseState
import com.syntxr.korediary.data.kotpref.LocalUser
import com.syntxr.korediary.data.source.remote.serializable.User
import com.syntxr.korediary.data.worker.UpdateUserWorker
import com.syntxr.korediary.domain.repository.UserRepository
import com.syntxr.korediary.utils.KEY_EMAIL
import com.syntxr.korediary.utils.KEY_NAME
import com.syntxr.korediary.utils.KEY_PASSWORD
import com.syntxr.korediary.utils.Network
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject

class UserRepoImpl @Inject constructor(
    private val context: Context,
    private val client : SupabaseClient
) : UserRepository {

    private val workManager = WorkManager.getInstance(context)
    private val constraint = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)

    override suspend fun register(
        email: String,
        password: String,
        username: String,
    ): Flow<ResponseState<Boolean>> = // = adalah return, karena butuh flow maka returnya flow
        flow {
            emit(ResponseState.Loading) // status loading
            try { // pakai try & catch biar tidak crash
                client.auth.signUpWith(Email) { // memanggil auth dari supabase
                    this.email = email
                    this.password = password
                    data = buildJsonObject { // menggunakan raw user meta data
                        put("username", username)
                    }
                }
                Toast.makeText(context, "Success register", Toast.LENGTH_SHORT).show()
                emit(ResponseState.Success(true)) // status success
            } catch (e: Exception) { // catch error
                emit(ResponseState.Error(e.message.toString())) // status error
            }
        }

    override suspend fun login(email: String, password: String): Flow<ResponseState<Boolean>> =
        flow {
            emit(ResponseState.Loading) // loading
            try {
                client.auth.signInWith(Email) { // login
                    this.email = email
                    this.password = password
                }
                val user = client.auth.currentUserOrNull() // mendapatkan data user yang login
                val publicUser = client.from("users") // memeriksa apakah user ada di table users supabase
                    .select{
                        filter {
                            User::uuid eq user?.id
                        }
                    }.decodeSingle<User>() // decode atau convert data ke class User

                LocalUser.apply { // menyimpan data ke lokal
                    uuid = publicUser.uuid
                    username = publicUser.username
                    this.email = email
                    this.password = password
                }
                Toast.makeText(context, "Success login", Toast.LENGTH_SHORT).show()
                emit(ResponseState.Success(true)) // success
            } catch (e: Exception) {
                emit(ResponseState.Error(e.message.toString())) // error
            }
        }

    override suspend fun update(name: String, email: String, password: String) {
        val updateBuilder = OneTimeWorkRequestBuilder<UpdateUserWorker>()

        updateBuilder.setInputData(
            Data.Builder()
                .putString(KEY_NAME, name)
                .putString(KEY_EMAIL, email)
                .putString(KEY_PASSWORD, password)
                .build()
        )
        updateBuilder.setConstraints(constraint.build())

        val updateRequest = updateBuilder.build()

        if (Network.checkConnectivity(context)){
            workManager.enqueueUniqueWork(
                LocalUser.uuid,
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                updateRequest
            )
        }else{
            Toast.makeText(context, "Make sure you have connection ╯︿╰", Toast.LENGTH_SHORT).show()
        }
    }
}
