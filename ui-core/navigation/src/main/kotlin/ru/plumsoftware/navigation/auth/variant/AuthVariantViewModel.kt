package ru.plumsoftware.navigation.auth.variant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import ru.plumsoftware.navigation.auth.variant.model.Effect
import ru.plumsoftware.navigation.auth.variant.model.Event

class AuthVariantViewModel : ViewModel() {
    val effect = MutableSharedFlow<Effect>()

    fun onEvent(event: Event) {
        when(event) {
            Event.Auth -> {
                viewModelScope.launch {
                    effect.emit(Effect.Auth)
                }
            }
            Event.Register -> {
                viewModelScope.launch {
                    effect.emit(Effect.Register)
                }
            }
        }
    }
}