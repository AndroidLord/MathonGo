package com.shubhamsinghbisht.quiz_answer.data.local

import android.content.Context
import com.shubhamsinghbisht.quiz_answer.data.mapper.toQuestionBank
import com.shubhamsinghbisht.quiz_answer.di.IoDispatcher
import com.shubhamsinghbisht.quiz_answer.domain.model.QuestionBank
import com.shubhamsinghbisht.quiz_answer.domain.repository.QuestionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

class AssetQuestionRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:IoDispatcher private val dispatcher: CoroutineDispatcher,
) : QuestionRepository {

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun loadQuestions(): Result<QuestionBank> = withContext(dispatcher) {
        runCatching {
            val exam = context.assets.open(ASSET_NAME).use { stream ->
                json.decodeFromStream<ExamDto>(stream)
            }
            exam.toQuestionBank()
        }.mapCatching { bank ->
            if (bank.questions.isEmpty()) {
                throw IllegalStateException("$ASSET_NAME contained no usable questions")
            }
            bank
        }
    }

    private companion object {
        const val ASSET_NAME = "data.json"

        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
            explicitNulls = false
        }
    }
}
