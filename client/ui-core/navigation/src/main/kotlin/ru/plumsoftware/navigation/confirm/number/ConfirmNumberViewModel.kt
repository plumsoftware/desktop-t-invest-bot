package ru.plumsoftware.navigation.confirm.number

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.plumsoftware.client.core.verify.phone.VerifyPhoneRepository
import ru.plumsoftware.navigation.confirm.number.model.Action
import ru.plumsoftware.navigation.confirm.number.model.Effect
import ru.plumsoftware.navigation.confirm.number.model.Event
import ru.plumsoftware.navigation.confirm.number.model.State
import kotlin.random.Random

class ConfirmNumberViewModel(
    private val verifyPhoneRepository: VerifyPhoneRepository
) : ViewModel() {
    val state = MutableStateFlow(State.default())
    val effect = MutableSharedFlow<Effect>()

    private val code by lazy {
        generateRandomDigits()
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnCodeChanged -> {
                state.update {
                    it.copy(code = event.code)
                }
            }

            Event.Back -> {
                viewModelScope.launch {
                    effect.emit(Effect.Back)
                }
            }

            Event.Confirm -> {
                val res = code == state.value.code

                if (res)
                    viewModelScope.launch {
                        effect.emit(Effect.Next)
                    }
            }
        }
    }

    fun onAction(action: Action) {
        when (action) {
            Action.SendCode -> {
                verifyPhoneRepository.sendCode(code = code, number = state.value.phoneNumber)
            }

            is Action.SetupPhoneNumber -> {
                state.update {
                    it.copy(phoneNumber = action.phone)
                }
            }
        }
    }

    private fun generateRandomDigits(length: Int = 4): String {
        val result = StringBuilder()

        repeat(length) {
            val randomDigit = Random.nextInt(1, 10)
            result.append(randomDigit)
        }

        return result.toString()
    }
}