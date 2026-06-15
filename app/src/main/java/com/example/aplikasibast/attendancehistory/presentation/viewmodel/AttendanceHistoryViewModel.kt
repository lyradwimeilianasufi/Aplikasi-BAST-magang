package com.example.aplikasibast.attendancehistory.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.aplikasibast.attendancehistory.domain.usecase.GetHistory

class AttendanceHistoryViewModel(
    private val getHistory: GetHistory
) : ViewModel() {
    private val _state = mutableStateOf(AttendanceHistoryState())
    val state: State<AttendanceHistoryState> = _state

    suspend fun getKehadiranById(id: Int) = repository.getKehadiranById(id)
}