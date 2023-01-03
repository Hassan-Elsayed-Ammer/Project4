package com.udacity.project4.locationreminders

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityReminderDescriptionBinding
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.utils.EXTRA_ReminderDataItem
import org.koin.android.ext.android.inject

/**
 * Activity that displays the reminder details after the user clicks on the notification
 */
class ReminderDescriptionActivity : AppCompatActivity() {

    val viewModel: ReminderDescriptionActivityViewModel by inject()

    companion object {
        fun newIntent(context: Context, reminderDataItem: ReminderDataItem): Intent {
            val intent = Intent(context, ReminderDescriptionActivity::class.java)
            intent.putExtra(EXTRA_ReminderDataItem, reminderDataItem)
            return intent
        }
    }

    private lateinit var binding: ActivityReminderDescriptionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_reminder_description
        )
        binding.reminderDataItem = intent.getSerializableExtra("ReminderDataItem") as ReminderDataItem?
        binding.edit.setOnClickListener{
            val ent = Intent(this, RemindersActivity::class.java)
            ent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            ent.putExtra("Id", binding.reminderDataItem!!)
            startActivity(ent)
        }
        binding.delete.setOnClickListener{
            viewModel.deleteReminderById(binding.reminderDataItem!!)
            Toast.makeText(this, "deleted", Toast.LENGTH_SHORT).show()
        }

    }
}
