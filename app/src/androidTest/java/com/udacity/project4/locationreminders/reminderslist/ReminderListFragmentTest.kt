package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
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
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
@RunWith(AndroidJUnit4::class)
class ReminderListFragmentTest: AutoCloseKoinTest() {

//    DONE: test the navigation of the fragments.
//    DONE: test the displayed data on the UI.
//    DONE: add testing for the error messages.
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repo : ReminderDataSource
    private lateinit var remindersListViewModel: RemindersListViewModel
    private lateinit var context: Application

    @Before
    fun init(){
        stopKoin()
        context = getApplicationContext()

        val myMod = module {
            single {
                SaveReminderViewModel(
                    context,
                    get() as ReminderDataSource
                )
            }

            viewModel {
                RemindersListViewModel(
                    context,
                    get() as ReminderDataSource
                )
            }

            single {
                LocalDB.createRemindersDao(context)
            }
            single {
                RemindersLocalRepository(get()) as ReminderDataSource
            }



        }
        startKoin {
            modules(listOf(myMod))
        }
        repo = get()

        runBlocking {
            repo.deleteAllReminders()
        }
        remindersListViewModel = RemindersListViewModel(context , repo)
    }

    @Test
    fun NoDataDisplayed() {
        launchFragmentInContainer<ReminderListFragment>(Bundle() , R.style.AppTheme)
        onView(withText("No Data")).check(matches(isDisplayed()))
    }

    @Test
    fun navigateToSaveReminderFragmentUsingFAB() {
        val sc = launchFragmentInContainer <ReminderListFragment>(Bundle() ,R.style.AppTheme )
        val navController = mock(NavController::class.java)
        sc.onFragment {
            Navigation.setViewNavController(it.view!! , navController)
        }

        onView(withId(R.id.addReminderFAB)).perform(click())

        verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder()
        )
    }

    @Test
    fun reminderListDisplayUI() {
        val reminder = ReminderDTO("Pharmacy", "Don't forget to buy medicine", "cairo", 30.3, 30.0)

        runBlocking {
            repo.saveReminder(reminder)
        }

        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        onView(withId(R.id.reminderssRecyclerView))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    hasDescendant(withText(reminder.title))
                )
            )
        onView(withText(reminder.title)).check(matches(isDisplayed()))

    }


    @Test
    fun noDataDisplayedOnUI() {
        val rem = ReminderDTO("Pharmacy" ,"Don't forget to buy medicine" , "cairo" , 30.3 , 30.0)

        runBlocking {
            repo.saveReminder(rem)
            repo.deleteAllReminders()
        }


        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        onView(withId(R.id.noDataTextView)).check(ViewAssertions.matches(isDisplayed()))
    }

}