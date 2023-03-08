package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var remindersList: MutableList<ReminderDTO> = mutableListOf()) : ReminderDataSource {

//    DONE: Create a fake data source to act as a double to the real data source
    private var returnError: Boolean = false

    fun setReturnError(newVal: Boolean){
        returnError = newVal
    }

    fun getReturnErrorValue() = returnError

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        //DONE("Return the reminders")
        return if (getReturnErrorValue()) { Result.Error("error") } else { Result.Success<List<ReminderDTO>>(remindersList)}
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        //DONE("save the reminder")
        remindersList.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        //DONE("return the reminder with the id")
        return when{
            getReturnErrorValue() -> { Result.Error("Error")}
            else -> {
                when (val rem = remindersList.find { it.id == id}){
                    null -> { Result.Error("This id not found")}
                    else -> { Result.Success(rem)}
                }
            }
        }
    }

    override suspend fun deleteAllReminders() {
        //DONE("delete all the reminders")
        remindersList.clear()
    }


}