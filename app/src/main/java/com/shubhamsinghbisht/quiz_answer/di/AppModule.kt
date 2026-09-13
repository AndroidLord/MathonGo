package com.shubhamsinghbisht.quiz_answer.di

import com.shubhamsinghbisht.quiz_answer.domain.model.QuestionType
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideQuestionFlowConfig(): QuestionFlowConfig =
        QuestionFlowConfig(
            supportedTypes = setOf(
                QuestionType.SINGLE_CORRECT,
                QuestionType.MULTIPLE_CORRECT,
                QuestionType.NUMERICAL,
            ),
        )
}
