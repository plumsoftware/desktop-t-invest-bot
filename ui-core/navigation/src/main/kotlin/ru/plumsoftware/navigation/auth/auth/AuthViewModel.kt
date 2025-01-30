package ru.plumsoftware.navigation.auth.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.plumsoftware.client.core.auth.AuthRepository
import ru.plumsoftware.navigation.auth.auth.model.Effect
import ru.plumsoftware.navigation.auth.auth.model.Event
import ru.plumsoftware.navigation.auth.auth.model.State
import ru.plumsoftware.net.core.model.response.UserResponseEither

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val state_ = MutableStateFlow(State.default())
    val state = state_.asStateFlow()

    val effect = MutableSharedFlow<Effect>()

    private val supervisorIOContext = Dispatchers.IO + SupervisorJob() + CoroutineName("IO")

    fun onEvent(event: Event) {
        when (event) {
            Event.Next -> {

                viewModelScope.launch(context = supervisorIOContext) {
                    val response = authRepository.getUserByPhone(phone = state_.value.phone.replace("+", ""))
                    when(response) {
                        is UserResponseEither.Error -> {
                            println("Error get user by phone is ${response.msg}")
                        }
                        is UserResponseEither.UserResponse -> {
                            withContext(Dispatchers.Main) {
                                effect.emit(Effect.Next)
                            }
                        }
                    }
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