package com.udacity.project4

import android.app.Activity
import android.app.Application
import androidx.lifecycle.Transformations.map
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorActivity
import com.udacity.project4.utils.EspressoIdlingResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers
import org.hamcrest.core.IsNot
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.KoinTest
import org.koin.test.get

@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest :
    KoinTest {
    // To close Koin after each test, integrate the auto close @after function in extended Koin tests.
    private lateinit var repo: ReminderDataSource
    private lateinit var context: Application
    // An idle resource that waits for all outstanding data bindings to be completed
    private val dataBindingResource = DataBindingIdlingResource()

    @get:Rule
    val activityRule = ActivityTestRule(RemindersActivity::class.java)
    // Get activity context
    private fun getActivity(activityScenario: ActivityScenario<RemindersActivity>): Activity? {
        var activity: Activity? = null
        activityScenario.onActivity {
            activity = it
        }
        return activity
    }
    // We'll use Koin to test our code just as we did to design it using the Service Locator Library.
    // In order to use the Koin-related code in our tests, we shall initialise it now.
    @Before
    fun init() {
        //stop the original app koin
        stopKoin()
        context = getApplicationContext()
        val myModule = module {
            viewModel { RemindersListViewModel(context, get() as ReminderDataSource) }
            single { SaveReminderViewModel(context, get() as ReminderDataSource) }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(context) }
        }
        // new koin module
        startKoin { modules(listOf(myModule)) }
        //Get our repository
        repo = get()
        // delete the data to start fresh
        runBlocking { repo.deleteAllReminders() }
    }

    // In order to be garbage collected and prevent memory leaks, deregister your idle resource.
    @After
    fun unregisterResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingResource)
    }

    @Before
    fun registerResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingResource)
    }
    // This function tests adding a reminder and displaying saved toast.
    @ExperimentalCoroutinesApi
    @Test
    fun showReminderSavedToast() = runBlocking{
        // GIVEN - Launch Reminder activity
        val scenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingResource.monitorActivity(scenario)
        // WHEN - We begin entering information for the reminder.
        Espresso.onView(ViewMatchers.withId(R.id.addReminderFAB)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.reminderTitle))
            .perform(ViewActions.typeText("TITLE1"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.reminderDescription))
            .perform(ViewActions.typeText("DESC1"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.selectLocation)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(com.google.android.material.R.id.snackbar_action))
            .perform(ViewActions.click())
        // Performing Long click on the map to select a location
        Espresso.onView(withId(R.id.googleMap)).perform(ViewActions.longClick())
        Espresso.onView(ViewMatchers.withId(R.id.save)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.saveReminder)).perform(ViewActions.click())
        // THEN - expect to have a Toast displaying reminder_saved String.
        Espresso.onView(ViewMatchers.withText(R.string.reminder_saved))
            .inRoot(RootMatchers.withDecorView(IsNot.not(Matchers.`is`(getActivity(scenario)?.window?.decorView))))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        scenario.close()
    }
    // We test adding a reminder in this method without a title.
    @Test
    fun showSnackAndEnterTitle(){
        // GIVEN - Launch Reminders Activity
        val scenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingResource.monitorActivity(scenario)
        // WHEN - click on add reminder and try to save the reminder without giving any inputs
        Espresso.onView(ViewMatchers.withId(R.id.addReminderFAB)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.saveReminder)).perform(ViewActions.click())
        // THEN - expect we have a SnackBar displaying err_enter_title
        Espresso.onView(ViewMatchers.withId(com.google.android.material.R.id.snackbar_text))
            .check(ViewAssertions.matches(ViewMatchers.withText(R.string.err_enter_title)))

        scenario.close()
    }
    // We test adding a reminder using this function without entering a location.
    @Test
    fun showSnackAndEnterLocation(){
        // GIVEN - Launch Reminders Activity
        val scenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingResource.monitorActivity(scenario)
        // WHEN - click on add reminder and try to save the reminder without giving a location
        Espresso.onView(ViewMatchers.withId(R.id.addReminderFAB)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.reminderTitle))
            .perform(ViewActions.typeText("TITLE1"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.saveReminder)).perform(ViewActions.click())
        // THEN - expect we have a SnackBar displaying err_select_location
        Espresso.onView(ViewMatchers.withId(com.google.android.material.R.id.snackbar_text))
            .check(ViewAssertions.matches(ViewMatchers.withText(R.string.err_select_location)))

        scenario.close()
    }
}
