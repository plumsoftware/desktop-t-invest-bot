package ru.plumsoftware.ui.presentation.screens.main

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import ru.plumsoftware.ui.presentation.screens.main.model.Effect
import ru.plumsoftware.ui.presentation.screens.main.model.Event

class MainViewModel : ViewModel() {

    val effect = MutableSharedFlow<Effect>()

    fun onEvent(event: Event) {
        when (event) {
            Event.OpenBiddingClick -> viewModelScope.launch { effect.emit(Effect.OpenBidding) }
            Event.OpenSandboxClick -> viewModelScope.launch { effect.emit(Effect.OpenSandbox) }
            Event.OpenSettingsClick -> viewModelScope.launch { effect.emit(Effect.OpenSettings) }
        }
    }
}