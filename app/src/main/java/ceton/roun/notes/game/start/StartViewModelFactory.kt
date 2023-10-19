package ceton.roun.notes.game.start

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class StartViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StartViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
