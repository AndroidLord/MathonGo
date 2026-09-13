package com.shubhamsinghbisht.quiz_answer.di

import com.shubhamsinghbisht.quiz_answer.data.local.AssetQuestionRepository
import com.shubhamsinghbisht.quiz_answer.domain.repository.QuestionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindQuestionRepository(impl: AssetQuestionRepository): QuestionRepository
}
