package ru.plumsoftware.navigation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.plumsoftware.navigation.register.model.Effect
import ru.plumsoftware.navigation.register.model.Event
import ru.plumsoftware.navigation.register.model.State

class RegisterViewModel : ViewModel() {
    private val state_ = MutableStateFlow(State.default())
    val state = state_.asStateFlow()
    val effect = MutableSharedFlow<Effect>()

    fun onEvent(event: Event) {
        when (event) {
            Event.Back -> {
                viewModelScope.launch {
                    effect.emit(Effect.Back)
                }
            }
            Event.Next -> {
                viewModelScope.launch {
                    effect.emit(Effect.Next)
                }
            }
            is Event.OnNameChanged -> {
                state_.update {
                    it.copy(name = event.name)
                }
            }
            is Event.OnPasswordChanged -> {
                state_.update {
                    it.copy(password = event.password)
                }
            }
        }
    }
}