package com.shubhamsinghbisht.quiz_answer.di

import com.shubhamsinghbisht.quiz_answer.domain.model.QuestionType

data class QuestionFlowConfig(
    val supportedTypes: Set<QuestionType>,
)
