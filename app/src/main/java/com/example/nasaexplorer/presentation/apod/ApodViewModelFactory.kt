package com.example.nasaexplorer.presentation.apod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nasaexplorer.data.repository.ApodRepository

class ApodViewModelFactory(
    private val repository: ApodRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(ApodViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ApodViewModel(repository) as T
        }

        throw IllegalArgumentException("ViewModel desconocido")
    }
}
