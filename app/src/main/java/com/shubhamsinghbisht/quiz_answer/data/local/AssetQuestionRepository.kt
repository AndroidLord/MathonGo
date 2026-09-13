package com.shubhamsinghbisht.quiz_answer.data.local

import android.content.Context
import com.shubhamsinghbisht.quiz_answer.data.mapper.toQuestionBank
import com.shubhamsinghbisht.quiz_answer.domain.model.QuestionBank
import com.shubhamsinghbisht.quiz_answer.domain.repository.QuestionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

/**
 * Reads the question bank from `assets/data.json`.
 *
 * Parsing runs on [dispatcher] (IO by default), streamed rather than read into a single String,
 * because the bundled file is a couple of megabytes.
 */
class AssetQuestionRepository(
    private val context: Context,
    private val assetName: String = ASSET_NAME,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : QuestionRepository {

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun loadQuestions(): Result<QuestionBank> = withContext(dispatcher) {
        runCatching {
            val exam = context.assets.open(assetName).use { stream ->
                json.decodeFromStream<ExamDto>(stream)
            }
            exam.toQuestionBank()
        }.mapCatching { bank ->
            if (bank.questions.isEmpty()) {
                throw IllegalStateException("$assetName contained no usable questions")
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
