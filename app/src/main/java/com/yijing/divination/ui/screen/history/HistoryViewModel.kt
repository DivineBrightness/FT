package com.yijing.divination.ui.screen.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yijing.divination.data.local.database.entity.RecordEntity
import com.yijing.divination.data.repository.RecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 历史记录界面 ViewModel
 *
 * 加载并展示所有历史占卜记录
 */
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val recordRepository: RecordRepository
) : ViewModel() {

    // UI 状态
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    /**
     * 加载历史记录
     */
    private fun loadHistory() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }

                recordRepository.getAllRecordsFlow().collect { records ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            records = records.sortedByDescending { record -> record.timestamp }
                        )
                    }
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
     * 删除记录
     */
    fun deleteRecord(record: RecordEntity) {
        viewModelScope.launch {
            try {
                recordRepository.deleteRecord(record)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "删除失败：${e.message}")
                }
            }
        }
    }

    /**
     * 删除所有记录
     */
    fun deleteAllRecords() {
        viewModelScope.launch {
            try {
                recordRepository.deleteAll()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "删除失败：${e.message}")
                }
            }
        }
    }
}

/**
 * 历史记录 UI 状态
 */
data class HistoryUiState(
    val isLoading: Boolean = false,
    val records: List<RecordEntity> = emptyList(),
    val errorMessage: String? = null
)
