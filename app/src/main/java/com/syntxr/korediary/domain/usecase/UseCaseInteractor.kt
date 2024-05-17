package com.syntxr.korediary.domain.usecase

import android.content.Context
import com.syntxr.korediary.domain.repository.DiaryRepository
import com.syntxr.korediary.domain.repository.UserRepository

class UseCaseInteractor(
    private val userRepo: UserRepository,
    private val diaryRepo: DiaryRepository, // abaikan dulu
    private val context: Context
) : GlobalUseCase {

    override val authUseCase : AuthUseCase
        get() = AuthUseCase(userRepo)

//    abaikan dulu
    override val diaryUseCase: DiaryUseCase
        get() = DiaryUseCase(diaryRepo, context)

}