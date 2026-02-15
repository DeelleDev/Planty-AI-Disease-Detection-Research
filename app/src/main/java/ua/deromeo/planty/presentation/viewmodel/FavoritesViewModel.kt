package ua.deromeo.planty.presentation.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ua.deromeo.planty.domain.usecase.IsFavoriteUseCase
import ua.deromeo.planty.domain.usecase.ToggleFavoriteUseCase

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val isFavoriteUseCase: IsFavoriteUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _id = MutableStateFlow<Long>(0)
    val id: StateFlow<Long> = _id.asStateFlow()

    private val _type = MutableStateFlow("")
    val type: StateFlow<String> = _type.asStateFlow()


    var isFavorite by mutableStateOf(false)

    fun loadFavoriteStatus(id: Long, type: String) {
        viewModelScope.launch {
            isFavorite = isFavoriteUseCase(id, type)
            _id.value = id
            _type.value = type
        }
    }

    fun toggleFavorite(context: Context, id: Long, type: String) {
        viewModelScope.launch {
            val added = toggleFavoriteUseCase(id, type)
            isFavorite = added
            Toast.makeText(
                context, if (added) "Додано в обране" else "Видалено з обраного", Toast.LENGTH_SHORT
            ).show()
        }
    }
}