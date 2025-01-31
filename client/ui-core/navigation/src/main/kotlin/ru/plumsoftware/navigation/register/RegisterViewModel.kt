package ru.plumsoftware.navigation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.plumsoftware.navigation.register.model.Effect
import ru.plumsoftware.navigation.register.model.Event
import ru.plumsoftware.navigation.register.model.State

class RegisterViewModel : ViewModel() {
    val state = MutableStateFlow(State.default())
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
                state.update {
                    it.copy(name = event.name)
                }
            }
            is Event.OnPasswordChanged -> {
                state.update {
                    it.copy(password = event.password)
                }
            }

            is Event.OnPhoneChanged -> {
                state.update {
                    it.copy(phone = event.phone)
                }
            }
            Event.PrivacyPolicy -> {
                viewModelScope.launch {
                    effect.emit(Effect.PrivacyPolicy)
                }
            }
        }
    }
}