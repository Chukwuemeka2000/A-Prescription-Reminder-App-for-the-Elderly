package me.codeenzyme.reminder

import android.content.ComponentName
import android.os.Build
import android.os.Bundle
import android.os.RemoteException
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.firebase.Timestamp
import me.codeenzyme.reminder.databinding.ActivityAlarmBinding
import java.util.*


class AlarmActivity : AppCompatActivity() {

    //remember to bind to media player to control media player

    //private val zenTone = ZenTone(channelMask = AudioFormat.CHANNEL_OUT_STEREO)

    private var title: String? = null
    private var message: String? = null
    private var interval: Long? = null
    private var dosage: Int? = null
    private var dosageType: String? = null

    private var viewBinding: ActivityAlarmBinding? = null

    private var mediaBrowser : MediaBrowserCompat? = null
    private var mediaController : MediaControllerCompat? = null

    private lateinit var viewModel: AlarmViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setShowWhenLocked(true)
        }

        //zenTone.play(440F, 100)

        viewModel = ViewModelProvider(this, MedicationHistoryViewModelFactory(MedicationHistoryRepositoryImpl()))[AlarmViewModel::class.java]

        viewBinding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(viewBinding?.root)

        title = intent.getStringExtra(ALARM_TITLE)
        message = intent.getStringExtra(ALARM_MESSAGE)
        interval = intent.getLongExtra(ALARM_INTERVAL, 0)
        dosage = intent.getIntExtra(ALARM_DOSAGE, 0)
        dosageType = intent.getStringExtra(ALARM_DOSAGE_TYPE)

        viewBinding?.let {
            it.title.text = title
            it.message.text = message

            YoYo.with(Techniques.Shake)
                .duration(1000)
                .repeat(Int.MAX_VALUE)
                .playOn(it.alarm)

            it.skip.setOnClickListener {
                viewModel.addMedicationHistory(
                    MedicationHistory(
                        false,
                        title,
                        message,
                        interval?.toInt(),
                        dosage,
                        dosageType,
                        UUID.randomUUID().toString(),
                        Timestamp.now()
                    )
                ) {

                }
                stopAlarm()
                finish()
            }

            it.complete.setOnClickListener {
                viewModel.addMedicationHistory(
                    MedicationHistory(
                        true,
                        title,
                        message,
                        interval?.toInt(),
                        dosage,
                        dosageType,
                        UUID.randomUUID().toString(),
                        Timestamp.now()
                    )
                ) {

                }
                stopAlarm()
                finish()
            }
        }

        mediaBrowser = MediaBrowserCompat(this, ComponentName(this, AlarmService::class.java), object :
            MediaBrowserCompat.ConnectionCallback() {
            override fun onConnected() {
                super.onConnected()
                try{
                    val token = mediaBrowser?.sessionToken
                    mediaController = MediaControllerCompat(this@AlarmActivity, token!!)
                    MediaControllerCompat.setMediaController(this@AlarmActivity, mediaController)

                    mediaController?.registerCallback(object : MediaControllerCompat.Callback(){
                        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                            super.onPlaybackStateChanged(state)

                            /*when(state?.state){
                                PlaybackStateCompat.STATE_PLAYING -> { playPause.setImageResource(R.drawable.pause_24); playState = 0 }

                                PlaybackStateCompat.STATE_PAUSED -> { playPause.setImageResource(R.drawable.play_24); playState = 1 }

                                PlaybackStateCompat.STATE_STOPPED -> { playPause.setImageResource(R.drawable.play_24); playState = 2 }

                                else -> {}
                            }*/
                        }
                    })
                }catch (e : RemoteException){
                    Log.e("browser service error", e.toString())
                }
            }

            override fun onConnectionSuspended() {
                super.onConnectionSuspended()
                Log.e("browser service error", "connection to service suspended")
            }

            override fun onConnectionFailed() {
                super.onConnectionFailed()
                Log.e("browser service error", "connection to service failed")
            }
        }, null)
        mediaBrowser?.connect()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewBinding = null
        //zenTone.stop()
        //zenTone.release()
    }

    fun stopAlarm() : Unit {
        mediaController?.transportControls?.stop()
    }
}