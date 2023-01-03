package com.udacity.project4.locationreminders

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.launch

class ReminderDescriptionActivityViewModel(
    private val dataSource: ReminderDataSource,
    application: Application
) : AndroidViewModel(application) {

    fun deleteReminderById(reminderData: ReminderDataItem) {
        viewModelScope.launch {
            dataSource.deleteById(reminderData.id)
        }
    }
}


