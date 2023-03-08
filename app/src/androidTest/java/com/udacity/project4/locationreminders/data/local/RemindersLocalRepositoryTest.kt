package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

//    DONE: Add testing implementation to the RemindersLocalRepository.kt
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var reminderLocalRepo : RemindersLocalRepository

    private lateinit var db: RemindersDatabase

    @Before
    fun init(){
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        reminderLocalRepo = RemindersLocalRepository(
            db.reminderDao(),
            Dispatchers.Main
        )
    }

    @Test
    fun saveAndRetrieveReminderById() = runBlocking{
        val rem = ReminderDTO("Pharmacy" , "Don't forget to buy medicine" , "cairo" , 30.1 , 30.1)
        reminderLocalRepo.saveReminder(rem)
        val res = reminderLocalRepo.getReminder(rem.id) as com.udacity.project4.locationreminders.data.dto.Result.Success
        assertThat(res is com.udacity.project4.locationreminders.data.dto.Result.Success , `is`(true))
        assertThat(res , CoreMatchers.notNullValue())
        assertThat(res.data.id , `is`(rem.id))
        assertThat(res.data.title , `is`(rem.title))
        assertThat(res.data.description , `is`(rem.description))
    }

    @Test
    fun deleteRemindersAndGetEmptyList() = runBlocking {
        val rem = ReminderDTO("Pharmacy" , "Don't forget to buy medicine" , "cairo" , 30.1 , 30.1)
        reminderLocalRepo.saveReminder(rem)

        reminderLocalRepo.deleteAllReminders()

        val res = reminderLocalRepo.getReminders()
        res as com.udacity.project4.locationreminders.data.dto.Result.Success
        assertThat(res.data, `is`(emptyList()))

    }

    @After
    fun terminateDb () = db.close()

}