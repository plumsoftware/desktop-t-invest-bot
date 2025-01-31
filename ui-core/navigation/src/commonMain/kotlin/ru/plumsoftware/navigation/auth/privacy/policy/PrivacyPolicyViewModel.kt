package ru.plumsoftware.navigation.auth.privacy.policy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import ru.plumsoftware.navigation.auth.privacy.policy.model.Effect
import ru.plumsoftware.navigation.auth.privacy.policy.model.Event

class PrivacyPolicyViewModel : ViewModel() {
    val effect = MutableSharedFlow<Effect>()

    fun onEvent(event: Event) {
        when (event) {
            Event.Read -> {
                viewModelScope.launch {
                    effect.emit(Effect.Read)
                }
            }
        }
    }
}