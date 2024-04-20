package com.example.presentation.base

import androidx.lifecycle.ViewModel
import com.example.presentation.base.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


open class BaseViewModel<T> : ViewModel() {
    private val _state = MutableStateFlow<Resource<T>?>(null)
    val state: StateFlow<Resource<T>?> = _state

    protected fun postState(newState: Resource<T>) {
        _state.value = newState
    }

}
