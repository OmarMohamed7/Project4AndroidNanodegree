package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat as assert
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsNull.nullValue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //DONE: provide testing to the RemindersListViewModel and its live data objects
    private lateinit var fakeRepo: FakeDataSource
    private lateinit var remindersListVM: RemindersListViewModel

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()


    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun initViewModel(){
        stopKoin()
        fakeRepo = FakeDataSource()
        remindersListVM = RemindersListViewModel(ApplicationProvider.getApplicationContext() , fakeRepo)

    }

    @Test
    fun showLoadingOnLoadingReminders() = mainCoroutineRule.runBlockingTest {
        assertTrue(remindersListVM.showLoading.getOrAwaitValue())
        mainCoroutineRule.pauseDispatcher()
        remindersListVM.loadReminders()
        mainCoroutineRule.resumeDispatcher()
        assertFalse(remindersListVM.showLoading.getOrAwaitValue())
    }



    @Test
    fun updateSnackBar(){
        mainCoroutineRule.pauseDispatcher()
        fakeRepo.setReturnError(true)
        remindersListVM.loadReminders()
        mainCoroutineRule.resumeDispatcher()
        Truth.assertThat(remindersListVM.showSnackBar.getOrAwaitValue()).isEqualTo("Error getting reminders")
    }

//    @Test
//    fun returnEmpty() = mainCoroutineRule.runBlockingTest {
//        fakeRepo.deleteAllReminders()
//        remindersListVM.loadReminders()
//        val res = remindersListVM.showSnackBar.value
//
//        assertThat(res, `is`(nullValue()))
//    }

    @After
    fun clear() = stopKoin()

}