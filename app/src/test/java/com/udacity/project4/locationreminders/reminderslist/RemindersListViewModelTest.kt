package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    private lateinit var remindersList: RemindersListViewModel
    private lateinit var data: FakeDataSource

    private val item1 = ReminderDTO("Reminder1", "Description1", "Location1", 1.0, 1.0,"1")
    private val item2 = ReminderDTO("Reminder2", "Description2", "location2", 1.0, 2.0, "2")
    private val item3 = ReminderDTO("Reminder3", "Description3", "location3", 1.0, 3.0, "3")

    var instantExecutorRule = InstantTaskExecutorRule()
    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutineRule = MainCoroutineRule()

    @Before
    fun model(){ stopKoin()
        data = FakeDataSource()
        remindersList = RemindersListViewModel(ApplicationProvider.getApplicationContext(), data)
    }

    @After
    fun clearData() = runBlockingTest{
        data.deleteAllReminders()
    }

    @Test
    fun invalidateShowNoDataShowNoDataIsTrue()= coroutineRule.runBlockingTest{
        data.deleteAllReminders()
        remindersList.loadReminders()
        MatcherAssert.assertThat(remindersList.remindersList.getOrAwaitValue().size, Is.`is`(0))
        MatcherAssert.assertThat(remindersList.showNoData.getOrAwaitValue(), Is.`is`(true))
    }

    @Test
    fun loadRemindersLoadsThreeReminders()= coroutineRule.runBlockingTest {
        data.deleteAllReminders()
        data.saveReminder(item1)
        data.saveReminder(item2)
        data.saveReminder(item3)
        remindersList.loadReminders()
        MatcherAssert.assertThat(remindersList.remindersList.getOrAwaitValue().size, Is.`is`(3))
        MatcherAssert.assertThat(remindersList.showNoData.getOrAwaitValue(), Is.`is`(false))

    }

    @Test
    fun loadRemindersCheckLoading()= coroutineRule.runBlockingTest{
        coroutineRule.pauseDispatcher()
        data.deleteAllReminders()
        data.saveReminder(item1)
        remindersList.loadReminders()

        MatcherAssert.assertThat(remindersList.showLoading.getOrAwaitValue(), Is.`is`(true))

        coroutineRule.resumeDispatcher()

        MatcherAssert.assertThat(remindersList.showLoading.getOrAwaitValue(), Is.`is`(true))
    }
    @Test
    fun loadRemindersShouldReturnError()= coroutineRule.runBlockingTest{
        data.returnError(true)
        remindersList.loadReminders()
        MatcherAssert.assertThat(
            remindersList.showSnackBar.getOrAwaitValue(),
            Is.`is`("no Reminder found")
        )
    }

}