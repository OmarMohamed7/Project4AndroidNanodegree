package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {


    //DONE: provide testing to the SaveReminderView and its live data objects
    private lateinit var remRepo: FakeDataSource
    private lateinit var vm: SaveReminderViewModel

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutinesRule = MainCoroutineRule()

    private val rem1 = ReminderDataItem("Pharmacy" , "Pharmacy" , "cairo" , 30.3 , 30.0)
    private val rem2 = ReminderDataItem("Pharmacy2" , "Pharmacy2" , "cairo2" , 30.3 , 30.0)


    @Before
    fun init(){
        remRepo = FakeDataSource()
        vm = SaveReminderViewModel(ApplicationProvider.getApplicationContext() , remRepo)
    }

    @Test
    fun validateReminderData()  {
        Truth.assertThat(vm.validateEnteredData(rem1)).isTrue()
    }

    @Test
    fun ValdiateEnteredData_EmptyTitleAndUpdateSnackBar(){
        val errorRem = ReminderDataItem("" , "test" , "test" , 3.2,2.3)

        Truth.assertThat(vm.validateEnteredData(errorRem)).isFalse()
        Truth.assertThat(vm.showSnackBarInt.getOrAwaitValue ()).isEqualTo(R.string.err_enter_title)
    }

    @Test
    fun saveReminder_showLoading(){
        mainCoroutinesRule.pauseDispatcher()
        vm.saveReminder(rem1)
        vm.saveReminder(rem2)
        Truth.assertThat(vm.showLoading.getOrAwaitValue()).isTrue()
        mainCoroutinesRule.resumeDispatcher()
        Truth.assertThat(vm.showLoading.getOrAwaitValue()).isFalse()
    }

    @After
    fun clear(){
        stopKoin()
    }

}