package com.yijing.divination.ui.screen.result

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yijing.divination.data.local.database.entity.RecordEntity
import com.yijing.divination.data.model.Hexagram
import com.yijing.divination.data.repository.RecordRepository
import com.yijing.divination.domain.algorithm.HexagramGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 结果展示界面 ViewModel
 *
 * 加载并展示占卜结果的详细信息
 */
@HiltViewModel
class ResultViewModel @Inject constructor(
    private val recordRepository: RecordRepository,
    private val hexagramGenerator: HexagramGenerator,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val recordId: Long = savedStateHandle.get<String>("recordId")?.toLongOrNull() ?: -1L

    // UI 状态
    private val _uiState = MutableStateFlow(ResultUiState())
    val uiState: StateFlow<ResultUiState> = _uiState.asStateFlow()

    init {
        loadResult()
    }

    /**
     * 加载占卜结果
     */
    private fun loadResult() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }

                if (recordId == -1L) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "无效的记录ID"
                        )
                    }
                    return@launch
                }

                // 加载记录
                val record = recordRepository.getRecordById(recordId)
                if (record == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "未找到占卜记录"
                        )
                    }
                    return@launch
                }

                // 加载本卦
                val originalHexagram = hexagramGenerator.getHexagramById(record.originalHexagramId)
                if (originalHexagram == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "未找到卦象数据"
                        )
                    }
                    return@launch
                }

                // 加载变卦（如果有）
                val changedHexagram = record.changedHexagramId?.let { id ->
                    hexagramGenerator.getHexagramById(id)
                }

                // 解析变爻位置
                val changingPositions = if (record.changingYaoPositions.isNullOrBlank()) {
                    emptyList()
                } else {
                    record.changingYaoPositions.split(",")
                        .mapNotNull { it.toIntOrNull() }
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        record = record,
                        originalHexagram = originalHexagram,
                        changedHexagram = changedHexagram,
                        changingYaoPositions = changingPositions
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "加载失败：${e.message}"
                    )
                }
            }
        }
    }

    /**
     * 更新笔记
     */
    fun updateNote(note: String) {
        viewModelScope.launch {
            try {
                val currentRecord = _uiState.value.record ?: return@launch
                val updatedRecord = currentRecord.copy(note = note.ifBlank { null })
                recordRepository.updateRecord(updatedRecord)

                _uiState.update { it.copy(record = updatedRecord) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "保存笔记失败：${e.message}")
                }
            }
        }
    }

    /**
     * 切换收藏状态
     */
    fun toggleFavorite() {
        viewModelScope.launch {
            try {
                val currentRecord = _uiState.value.record ?: return@launch
                val updatedRecord = currentRecord.copy(isFavorite = !currentRecord.isFavorite)
                recordRepository.updateRecord(updatedRecord)

                _uiState.update { it.copy(record = updatedRecord) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "更新收藏失败：${e.message}")
                }
            }
        }
    }

    /**
     * 删除记录
     */
    fun deleteRecord(onDeleted: () -> Unit) {
        viewModelScope.launch {
            try {
                val currentRecord = _uiState.value.record ?: return@launch
                recordRepository.deleteRecord(currentRecord)
                onDeleted()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "删除失败：${e.message}")
                }
            }
        }
    }
}

/**
 * 结果展示 UI 状态
 */
data class ResultUiState(
    val isLoading: Boolean = false,
    val record: RecordEntity? = null,
    val originalHexagram: Hexagram? = null,
    val changedHexagram: Hexagram? = null,
    val changingYaoPositions: List<Int> = emptyList(),
    val errorMessage: String? = null
)
