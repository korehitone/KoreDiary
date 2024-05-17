package com.syntxr.korediary.di

import android.content.Context
import androidx.room.Room
import com.syntxr.korediary.BuildConfig
import com.syntxr.korediary.data.repository.DiaryRepoImpl
import com.syntxr.korediary.data.repository.UserRepoImpl
import com.syntxr.korediary.data.source.local.DiaryDatabase
import com.syntxr.korediary.domain.repository.DiaryRepository
import com.syntxr.korediary.domain.repository.UserRepository
import com.syntxr.korediary.domain.usecase.GlobalUseCase
import com.syntxr.korediary.domain.usecase.UseCaseInteractor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSupaClient(): SupabaseClient = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL, // supabase url di sini
        supabaseKey = BuildConfig.SUPABASE_API_KEY // supabase key disini
    ) {
        install(Auth) // agar bisa menggunakan auth dari supabase
        install(Postgrest) // agar bisa menggunakan table dari supabase
        install(Realtime) // agar bisa menggunakan realtime dari supabase
    }

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ) = Room.databaseBuilder(
        context,
        DiaryDatabase::class.java,
        DiaryDatabase.DB_NAME
    ).build()

    @Provides
    @Singleton
    fun provideUserRepo(client : SupabaseClient, @ApplicationContext context: Context): UserRepository =
        UserRepoImpl(context, client)

    @Provides
    @Singleton
    fun provideDiaryRepo(client: SupabaseClient, db: DiaryDatabase, @ApplicationContext context: Context) : DiaryRepository =
        DiaryRepoImpl(context, client, db.dao)

    @Provides
    @Singleton
    fun provideUseCase(
        user: UserRepository,
        diary: DiaryRepository,
        @ApplicationContext context: Context
    ): GlobalUseCase = UseCaseInteractor(user, diary, context)

}