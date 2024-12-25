package ru.plumsoftware.ui.presentation.screens.settings

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import ru.plumsoftware.core.settings.model.ApiTokens
import ru.plumsoftware.core.settings.model.Settings
import ru.plumsoftware.core.settings.repository.SettingsRepository
import ru.plumsoftware.ui.presentation.screens.settings.model.Effect
import ru.plumsoftware.ui.presentation.screens.settings.model.Event
import ru.plumsoftware.ui.presentation.screens.settings.model.Model

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

    val effect = MutableSharedFlow<Effect>()
    val model = MutableStateFlow(Model())

    fun onEvent(event: Event) {
        when (event) {
            Event.Back -> {
                viewModelScope.launch {
                    effect.emit(Effect.Back)
                }
            }

            is Event.ChangeIsDark -> {
                model.update {
                    it.copy(isDark = event.isDark)
                }
            }

            is Event.ChangeSandboxToken -> {
                model.update {
                    it.copy(sandboxToken = event.sandboxToken)
                }
            }

            is Event.ChangeToken -> {
                model.update {
                    it.copy(token = event.token)
                }
            }

            Event.SaveSettings -> {
                viewModelScope.launch(Dispatchers.IO + SupervisorJob()) {
                    settingsRepository.saveSettings(
                        Settings(
                            apiTokens = ApiTokens(
                                sandboxToken = model.value.sandboxToken,
                                token = model.value.token
                            ),
                            isDark = model.value.isDark
                        )
                    )
                }
            }
        }
    }
}