package com.shubhamsinghbisht.quiz_answer.domain.repository

import com.shubhamsinghbisht.quiz_answer.domain.model.QuestionBank

interface QuestionRepository {
    /**
     * Loads and flattens the bundled question bank. Never throws: a malformed or missing source
     * surfaces as [Result.failure] so the UI can show an error state instead of crashing.
     */
    suspend fun loadQuestions(): Result<QuestionBank>
}
