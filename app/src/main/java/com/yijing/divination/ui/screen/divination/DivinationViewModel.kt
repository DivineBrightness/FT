package com.yijing.divination.ui.screen.divination

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yijing.divination.data.local.database.entity.RecordEntity
import com.yijing.divination.data.model.CoinTossResult
import com.yijing.divination.data.model.DivinationResult
import com.yijing.divination.data.repository.RecordRepository
import com.yijing.divination.domain.algorithm.CoinTossAlgorithm
import com.yijing.divination.domain.algorithm.HexagramGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * 占卜界面状态
 */
data class DivinationUiState(
    val question: String = "",                          // 占卜问题
    val currentStep: Int = 0,                           // 当前步骤 (0-5)
    val tossResults: List<CoinTossResult> = emptyList(), // 投掷结果
    val isProcessing: Boolean = false,                  // 是否正在处理
    val divinationResult: DivinationResult? = null,     // 占卜结果
    val savedRecordId: Long? = null,                    // 保存的记录 ID
    val error: String? = null                           // 错误信息
) {
    val isComplete: Boolean
        get() = currentStep >= 6

    val canToss: Boolean
        get() = currentStep < 6 && !isProcessing
}

/**
 * 占卜 ViewModel
 *
 * 管理占卜流程：
 * 1. 用户输入问题
 * 2. 执行 6 次铜钱投掷
 * 3. 生成卦象
 * 4. 保存记录
 */
@HiltViewModel
class DivinationViewModel @Inject constructor(
    private val coinTossAlgorithm: CoinTossAlgorithm,
    private val hexagramGenerator: HexagramGenerator,
    private val recordRepository: RecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DivinationUiState())
    val uiState: StateFlow<DivinationUiState> = _uiState.asStateFlow()

    /**
     * 更新占卜问题
     */
    fun updateQuestion(question: String) {
        _uiState.update { it.copy(question = question) }
    }

    /**
     * 执行一次铜钱投掷
     */
    fun performToss() {
        if (!_uiState.value.canToss) return

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }

            try {
                // 投掷铜钱
                val result = coinTossAlgorithm.toss()

                // 添加到结果列表
                val newResults = _uiState.value.tossResults + result
                val newStep = _uiState.value.currentStep + 1

                _uiState.update {
                    it.copy(
                        tossResults = newResults,
                        currentStep = newStep,
                        isProcessing = false
                    )
                }

                // 如果完成 6 次投掷，生成卦象
                if (newStep == 6) {
                    generateHexagram(newResults)
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "投掷失败: ${e.message}",
                        isProcessing = false
                    )
                }
            }
        }
    }

    /**
     * 摇一摇投掷（使用传感器数据）
     */
    fun performShakeToss(sensorData: FloatArray? = null) {
        if (!_uiState.value.canToss) return

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }

            try {
                val result = coinTossAlgorithm.tossWithSensorData(sensorData)

                val newResults = _uiState.value.tossResults + result
                val newStep = _uiState.value.currentStep + 1

                _uiState.update {
                    it.copy(
                        tossResults = newResults,
                        currentStep = newStep,
                        isProcessing = false
                    )
                }

                if (newStep == 6) {
                    generateHexagram(newResults)
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "投掷失败: ${e.message}",
                        isProcessing = false
                    )
                }
            }
        }
    }

    /**
     * 生成卦象
     */
    private suspend fun generateHexagram(tossResults: List<CoinTossResult>) {
        try {
            val result = hexagramGenerator.generateDivinationResult(tossResults)

            _uiState.update {
                it.copy(divinationResult = result)
            }

        } catch (e: Exception) {
            _uiState.update {
                it.copy(error = "生成卦象失败: ${e.message}")
            }
        }
    }

    /**
     * 保存占卜记录
     */
    fun saveRecord() {
        val state = _uiState.value
        val result = state.divinationResult ?: return

        viewModelScope.launch {
            try {
                val recordEntity = RecordEntity(
                    timestamp = System.currentTimeMillis(),
                    question = state.question.takeIf { it.isNotBlank() },
                    originalHexagramId = result.originalHexagram.id,
                    originalHexagramName = result.originalHexagram.name,
                    yaoResults = Json.encodeToString(
                        result.tossResults.map { it.value }
                    ),
                    changedHexagramId = result.changedHexagram?.id,
                    changedHexagramName = result.changedHexagram?.name,
                    changingYaoPositions = if (result.changingYaoPositions.isNotEmpty()) {
                        Json.encodeToString(result.changingYaoPositions)
                    } else null,
                    note = null,
                    isFavorite = false
                )

                val recordId = recordRepository.insertRecord(recordEntity)

                _uiState.update {
                    it.copy(savedRecordId = recordId)
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "保存记录失败: ${e.message}")
                }
            }
        }
    }

    /**
     * 重新开始占卜
     */
    fun reset() {
        _uiState.value = DivinationUiState()
    }

    /**
     * 清除错误
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
