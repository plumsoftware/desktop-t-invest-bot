package ru.plumsoftware.navigation.confirm.number

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.plumsoftware.PlatformSpecific
import ru.plumsoftware.navigation.confirm.number.model.Action
import ru.plumsoftware.navigation.confirm.number.model.Effect
import ru.plumsoftware.navigation.confirm.number.model.Event
import ru.plumsoftware.navigation.confirm.number.model.State
import kotlin.random.Random

class ConfirmNumberViewModel(
    private val platformSpecific: PlatformSpecific
) : ViewModel() {
    private val state_ = MutableStateFlow(State.default())
    val state = state_.asStateFlow()
    val effect = MutableSharedFlow<Effect>()

    private val code by lazy {
        generateRandomDigits()
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnCodeChanged -> {
                state_.update {
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
                platformSpecific.sendCode()
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