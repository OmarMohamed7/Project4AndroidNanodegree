package com.udacity.project4.locationreminders.data.local

import android.app.Application
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

//    DONE: Add testing implementation to the RemindersDao.kt
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private lateinit var db : RemindersDatabase

    @Before
    fun initDb(){
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @Test
    fun saveReminder_GetByID() = runBlockingTest {
        val rem = ReminderDTO("Pharmacy" , "Don't forget to buy medicine" , "cairo" , 30.1 , 30.1)
        db.reminderDao().saveReminder(rem)

        val res = db.reminderDao().getReminderById(rem.id)
        assertThat(res , notNullValue())
        assertThat(res?.id , `is`(rem.id))
        assertThat(res?.title , `is`(rem.title))
        assertThat(res?.description , `is`(rem.description))
    }

    @Test
    fun getAllReminders() = runBlockingTest{
        val rem1 = ReminderDTO("Pharmacy" , "Don't forget to buy medicine" , "cairo" , 30.1 , 30.1)
        val rem2 = ReminderDTO("Market" , "Don't forget to buy fruits" , "cairo" , 30.1 , 30.1)

        db.reminderDao().saveReminder(rem1)
        db.reminderDao().saveReminder(rem2)

        val res = db.reminderDao().getReminders()

        assertThat(res, `is`(notNullValue()))

    }

    @Test
    fun deleteAllReminders() = runBlockingTest{
        val rem1 = ReminderDTO("Pharmacy" , "Don't forget to buy medicine" , "cairo" , 30.1 , 30.1)
        val rem2 = ReminderDTO("Market" , "Don't forget to buy fruits" , "cairo" , 30.1 , 30.1)

        db.reminderDao().saveReminder(rem1)
        db.reminderDao().saveReminder(rem2)

        db.reminderDao().deleteAllReminders()

        val res = db.reminderDao().getReminders()

        assertThat(res, `is`(notNullValue()))
        assertThat(res, `is`(emptyList()))

    }


    @After
    fun terminateDB() = db.close()
}