package com.syntxr.korediary.domain.usecase

interface GlobalUseCase {
    val authUseCase : AuthUseCase
    val diaryUseCase : DiaryUseCase
}