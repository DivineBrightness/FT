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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 占卜界面 ViewModel
 *
 * 管理占卜流程的状态和业务逻辑
 */
@HiltViewModel
class DivinationViewModel @Inject constructor(
    private val coinTossAlgorithm: CoinTossAlgorithm,
    private val hexagramGenerator: HexagramGenerator,
    private val recordRepository: RecordRepository
) : ViewModel() {

    // UI 状态
    private val _uiState = MutableStateFlow(DivinationUiState())
    val uiState: StateFlow<DivinationUiState> = _uiState.asStateFlow()

    /**
     * 开始占卜流程
     */
    fun startDivination() {
        if (_uiState.value.divinationState != DivinationState.IDLE) {
            return // 已经在进行中
        }

        _uiState.update {
            it.copy(
                divinationState = DivinationState.TOSSING,
                currentTossIndex = 0,
                tossResults = emptyList(),
                errorMessage = null
            )
        }

        // 开始第一次投掷
        performNextToss()
    }

    /**
     * 执行下一次投掷
     */
    private fun performNextToss() {
        viewModelScope.launch {
            try {
                // 模拟投掷动画延迟
                delay(500)

                val result = coinTossAlgorithm.toss()
                val currentResults = _uiState.value.tossResults + result
                val nextIndex = _uiState.value.currentTossIndex + 1

                _uiState.update {
                    it.copy(
                        tossResults = currentResults,
                        currentTossIndex = nextIndex
                    )
                }

                // 检查是否完成6次投掷
                if (nextIndex >= 6) {
                    completeDivination()
                } else {
                    // 延迟后自动进行下一次投掷
                    delay(800)
                    performNextToss()
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        divinationState = DivinationState.ERROR,
                        errorMessage = "投掷失败：${e.message}"
                    )
                }
            }
        }
    }

    /**
     * 完成占卜，生成卦象
     */
    private fun completeDivination() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(divinationState = DivinationState.GENERATING) }

                // 生成卦象
                val result = hexagramGenerator.generateDivinationResult(
                    _uiState.value.tossResults
                )

                // 保存记录
                val recordId = saveRecord(result)

                _uiState.update {
                    it.copy(
                        divinationState = DivinationState.COMPLETED,
                        divinationResult = result,
                        savedRecordId = recordId
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        divinationState = DivinationState.ERROR,
                        errorMessage = "生成卦象失败：${e.message}"
                    )
                }
            }
        }
    }

    /**
     * 保存占卜记录到数据库
     */
    private suspend fun saveRecord(result: DivinationResult): Long {
        val record = RecordEntity(
            timestamp = System.currentTimeMillis(),
            question = null,
            originalHexagramId = result.originalHexagram.id,
            originalHexagramName = result.originalHexagram.name,
            yaoResults = result.tossResults.joinToString(",") { it.value.toString() },
            changedHexagramId = result.changedHexagram?.id,
            changedHexagramName = result.changedHexagram?.name,
            changingYaoPositions = result.changingYaoPositions.joinToString(","),
            note = null,
            isFavorite = false
        )

        return recordRepository.insertRecord(record)
    }

    /**
     * 重置占卜
     */
    fun reset() {
        _uiState.update { DivinationUiState() }
    }

    /**
     * 手动投掷（用于用户手动点击）
     */
    fun manualToss() {
        if (_uiState.value.divinationState != DivinationState.IDLE) {
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(divinationState = DivinationState.TOSSING)
                }

                delay(300) // 短暂延迟模拟动画

                val result = coinTossAlgorithm.toss()
                val currentResults = _uiState.value.tossResults + result
                val nextIndex = currentResults.size

                _uiState.update {
                    it.copy(
                        tossResults = currentResults,
                        currentTossIndex = nextIndex,
                        divinationState = if (nextIndex >= 6) DivinationState.GENERATING else DivinationState.IDLE
                    )
                }

                // 如果完成6次，生成卦象
                if (nextIndex >= 6) {
                    completeDivination()
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        divinationState = DivinationState.ERROR,
                        errorMessage = "投掷失败：${e.message}"
                    )
                }
            }
        }
    }
}

/**
 * 占卜 UI 状态
 */
data class DivinationUiState(
    val divinationState: DivinationState = DivinationState.IDLE, // 当前状态
    val currentTossIndex: Int = 0,                          // 当前投掷次数（0-6）
    val tossResults: List<CoinTossResult> = emptyList(),    // 投掷结果列表
    val divinationResult: DivinationResult? = null,         // 占卜结果（卦象）
    val savedRecordId: Long? = null,                        // 保存的记录ID
    val errorMessage: String? = null                        // 错误消息
)

/**
 * 占卜状态
 */
enum class DivinationState {
    IDLE,           // 空闲，等待开始
    TOSSING,        // 投掷中
    GENERATING,     // 生成卦象中
    COMPLETED,      // 完成
    ERROR           // 错误
}
