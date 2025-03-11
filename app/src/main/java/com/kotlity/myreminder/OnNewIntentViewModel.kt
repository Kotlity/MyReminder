package com.kotlity.myreminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlity.core.notification.NOTIFICATION_ACTION
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class OnNewIntentViewModel: ViewModel() {

    private val _shouldNavigateToRemindersScreen = Channel<Boolean>()
    val shouldNavigateToRemindersScreenFlow = _shouldNavigateToRemindersScreen.receiveAsFlow()

    fun navigateToRemindersScreenIfNeeded(action: String?) {
        if (action == NOTIFICATION_ACTION) {
            viewModelScope.launch { _shouldNavigateToRemindersScreen.send(true) }
        }
    }
}