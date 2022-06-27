package me.codeenzyme.reminder

import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import me.codeenzyme.reminder.databinding.ActivityAlarmBinding


class AlarmActivity : AppCompatActivity() {

    //private val zenTone = ZenTone(channelMask = AudioFormat.CHANNEL_OUT_STEREO)
    private lateinit var player: MediaPlayer

    private var title: String? = null
    private var message: String? = null

    private var viewBinding: ActivityAlarmBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //zenTone.play(440F, 100)

        val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        player = MediaPlayer.create(this, notification)
        player.isLooping = true
        player.start()

        viewBinding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(viewBinding?.root)

        title = intent.getStringExtra(ALARM_TITLE)
        message = intent.getStringExtra(ALARM_MESSAGE)

        viewBinding?.let {
            it.title.text = title
            it.message.text = message

            YoYo.with(Techniques.Shake)
                .duration(1000)
                .repeat(Int.MAX_VALUE)
                .playOn(it.alarm)

            it.skip.setOnClickListener {
                finish()
            }

            it.complete.setOnClickListener {
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewBinding = null
        player.stop()
        player.release()
        //zenTone.stop()
        //zenTone.release()
    }
}