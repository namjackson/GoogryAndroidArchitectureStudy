package com.namjackson.archstudy.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namjackson.archstudy.data.source.Result

import com.namjackson.archstudy.util.Event
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    protected val _showToastEvent = MutableLiveData<Event<String>>()
    val showToastEvent: LiveData<Event<String>>
        get() = _showToastEvent

    protected var _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading


    protected fun launchDataLoad(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try {
                _isLoading.value = true
                block()
            } catch (error: Exception) {
                _showToastEvent.value = Event(error.message ?: "")
            } finally {
                _isLoading.value = false
            }
        }
    }

    protected fun showError(error: String) {
        _isLoading.value = false
        _showToastEvent.value = Event(error)
    }

    protected fun <T> resultLoad(result: Result<T>, block: (result: T) -> Unit) {
        when (result) {
            is Result.Success -> {
                block(result.data)
            }
            is Result.Error -> {
                showError(result.exception.toString())
                _isLoading.value = false
            }
            is Result.Loading ->
                _isLoading.value = true
        }
    }
}