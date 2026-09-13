package com.shubhamsinghbisht.quiz_answer.domain.repository

import com.shubhamsinghbisht.quiz_answer.domain.model.QuestionBank

interface QuestionRepository {
    suspend fun loadQuestions(): Result<QuestionBank>
}
