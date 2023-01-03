package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {
    // testing implementation to the RemindersDao.kt
    private lateinit var database: RemindersDatabase

    private val item1 = ReminderDTO("Reminder1", "Description1", "Location1", 1.0, 1.0,"1")
    private val item2 = ReminderDTO("Reminder2", "Description2", "location2", 2.0, 2.0, "2")
    private val item3 = ReminderDTO("Reminder3", "Description3", "location3", 3.0, 3.0, "3")
    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        // using an in-memory database because the information stored here disappears when the process is destroy
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()


    @Test
    fun insertAll() = runBlockingTest {
        database.reminderDao().saveReminder(item1)
        database.reminderDao().saveReminder(item2)
        database.reminderDao().saveReminder(item3)
        val loaded = database.reminderDao().getReminders()
        assertThat(loaded.size, `is`(3))
    }

    @Test
    fun insertReminderAndGetIt() = runBlockingTest {
        database.reminderDao().saveReminder(item1)
        val load = database.reminderDao().getReminderById(item1.id)
        assertThat<ReminderDTO>(load as ReminderDTO, notNullValue())
        assertThat(load.title, `is`(item1.title))
        assertThat(load.description, `is`(item1.description))
        assertThat(load.location, `is`(item1.location))
        assertThat(load.latitude, `is`(item1.latitude))
        assertThat(load.longitude, `is`(item1.longitude))
        assertThat(load.id, `is`(item1.id))
    }

    @Test
    fun insertAllAndDeleteAll()= runBlockingTest{
        database.reminderDao().saveReminder(item1)
        database.reminderDao().saveReminder(item2)
        database.reminderDao().saveReminder(item3)
        database.reminderDao().deleteAllReminders()
        val load = database.reminderDao().getReminders()
        assertThat(load.size, `is`(0))
    }

    @Test
    fun insertRemindersAndDeleteReminderById()= runBlockingTest{
        database.reminderDao().saveReminder(item1)
        database.reminderDao().saveReminder(item2)
        database.reminderDao().saveReminder(item3)
        database.reminderDao().deleteById(item1.id)
        val load = database.reminderDao().getReminders()
        assertThat(load.size, `is`(2))
        assertThat(load[0].id, `is` (item2.id))
    }


    @Test
    fun returnsError()= runBlockingTest{
        // Insert all items
        database.reminderDao().saveReminder(item1)
        database.reminderDao().saveReminder(item2)
        database.reminderDao().saveReminder(item3)
        // Delete items by Id & Try to retrieve the same items we deleted by Id
        database.reminderDao().deleteById(item1.id)
        //  The value we should receive should be null Value
        val load = database.reminderDao().getReminders()
        assertThat(load.size, `is`(2))
        assertThat(load[0].id, `is` (item2.id))
    }

}