package ru.plumsoftware.navigation.auth.variant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import ru.plumsoftware.navigation.auth.variant.model.Effect

class AuthVariantViewModel : ViewModel() {
    val effect = MutableSharedFlow<Effect>()

    fun onEvent(event: ru.plumsoftware.navigation.auth.variant.model.Event) {
        when(event) {
            ru.plumsoftware.navigation.auth.variant.model.Event.Auth -> {
                viewModelScope.launch {
                    effect.emit(Effect.Auth)
                }
            }
            ru.plumsoftware.navigation.auth.variant.model.Event.Register -> {
                viewModelScope.launch {
                    effect.emit(Effect.Register)
                }
            }
        }
    }
}