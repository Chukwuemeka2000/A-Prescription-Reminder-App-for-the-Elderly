package me.codeenzyme.reminder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import me.codeenzyme.reminder.databinding.ActivityAlarmBinding

class AlarmActivity : AppCompatActivity() {

    private var title: String? = null
    private var message: String? = null

    private var viewBinding: ActivityAlarmBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(viewBinding?.root)

        title = intent.getStringExtra(ALARM_TITLE)
        message = intent.getStringExtra(ALARM_MESSAGE)

        viewBinding?.let {
            it.title.text = title
            it.message.text = message
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewBinding = null
    }
}