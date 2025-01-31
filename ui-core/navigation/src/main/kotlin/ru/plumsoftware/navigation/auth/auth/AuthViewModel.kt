package ru.plumsoftware.navigation.auth.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.plumsoftware.client.core.auth.AuthRepository
import ru.plumsoftware.navigation.auth.auth.model.Effect
import ru.plumsoftware.navigation.auth.auth.model.Event
import ru.plumsoftware.navigation.auth.auth.model.State
import ru.plumsoftware.net.core.model.receive.PasswordMatchReceive
import ru.plumsoftware.net.core.model.response.UserResponseEither

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    val state = MutableStateFlow(State.default())

    val effect = MutableSharedFlow<Effect>()

    private val supervisorIOContext = Dispatchers.IO + SupervisorJob() + CoroutineName("IO")

    fun onEvent(event: Event) {
        when (event) {
            Event.Next -> {
                viewModelScope.launch(context = supervisorIOContext) {
                    withContext(Dispatchers.Main) {
                        loading(true)
                    }
                    val response = try {
                        authRepository.getUserByPhone(phone = state.value.phone)
                    } catch (e: Exception) {
                        UserResponseEither.Error(msg = e.message ?: "Error")
                    }
                    when (response) {
                        is UserResponseEither.Error -> {
                            loading(false)
                            withContext(Dispatchers.Main) {
                                effect.emit(Effect.ShowSnackBar(msg = response.msg))
                            }
                        }

                        is UserResponseEither.UserResponse -> {
                            val id = response.id
                            val passwordMatch = authRepository.getPasswordMatch(
                                passwordMatchReceive = PasswordMatchReceive(
                                    id = id,
                                    password = state.value.password
                                )
                            )

                            if (passwordMatch.value in 200..299) {
                                loading(false)
                                withContext(Dispatchers.Main) {
                                    effect.emit(Effect.Next)
                                }
                            } else {
                                loading(false)
                                withContext(Dispatchers.Main) {
                                    effect.emit(Effect.ShowSnackBar(msg = passwordMatch.description))
                                }
                            }
                        }
                    }
                }
            }

            is Event.OnPhoneChange -> {
                state.update {
                    it.copy(phone = event.phone)
                }
            }

            is Event.OnPasswordChange -> {
                state.update {
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

    private fun loading(loading: Boolean) {
        state.update {
            it.copy(isLoading = loading)
        }
    }
}