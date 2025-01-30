package ru.plumsoftware.navigation.auth.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.plumsoftware.navigation.auth.auth.model.Effect
import ru.plumsoftware.navigation.auth.auth.model.Event
import ru.plumsoftware.navigation.auth.auth.model.State

class AuthViewModel : ViewModel() {
    private val state_ = MutableStateFlow(State.default())
    val state = state_.asStateFlow()

    val effect = MutableSharedFlow<Effect>()

    fun onEvent(event: Event) {
        when (event) {
            Event.Next -> {
                viewModelScope.launch {
                    effect.emit(Effect.Next)
                }
            }

            is Event.OnPhoneChange -> {
                state_.update {
                    it.copy(phone = event.phone)
                }
            }

            is Event.OnPasswordChange -> {
                state_.update {
                    it.copy(password = event.password)
                }
            }

            Event.Back -> {
                viewModelScope.launch {
                    effect.emit(Effect.Back)
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