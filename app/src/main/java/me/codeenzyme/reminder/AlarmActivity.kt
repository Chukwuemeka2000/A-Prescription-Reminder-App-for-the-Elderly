package me.codeenzyme.reminder

import android.media.AudioFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.nisrulz.zentone.ZenTone
import me.codeenzyme.reminder.databinding.ActivityAlarmBinding

class AlarmActivity : AppCompatActivity() {

    val zenTone = ZenTone(channelMask = AudioFormat.CHANNEL_OUT_STEREO)

    private var title: String? = null
    private var message: String? = null

    private var viewBinding: ActivityAlarmBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        zenTone.play(440F, 100)

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
        zenTone.stop()
        zenTone.release()
    }
}