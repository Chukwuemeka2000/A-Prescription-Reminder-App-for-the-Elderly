package me.codeenzyme.reminder

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.media.AudioFocusRequestCompat
import androidx.media.AudioManagerCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.lang.Runnable
import java.time.Duration
import java.util.*

class AlarmService : MediaBrowserServiceCompat() {

    private var handler : Handler? = null
    private var mediaSession : MediaSessionCompat? = null
    private var mediaController : MediaControllerCompat? = null

    private var playback : PlaybackStateCompat.Builder? = null
    private var mediaPlayer : MediaPlayer? = null

    private var audioManager : AudioManager? = null
    private var audioFocus : AudioFocusRequestCompat? = null

    private lateinit var vibrationService : Vibrator

    override fun onCreate() {
        super.onCreate()

        vibrationService = getSystemService(VIBRATOR_SERVICE) as Vibrator
        handler = Handler(Looper.getMainLooper())

        val defaultToneString = runBlocking {
            dataPreferences.data.map {settings ->
                settings[stringPreferencesKey(DEFAULT_RINGTONE_PREFERENCE_KEY)] ?: ""
            }.first()
        }
        val defaultToneUri = if (defaultToneString.isBlank()){
            RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM)
        }else{
            Uri.parse(defaultToneString)
        }

        val audioAttrs = AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_ALARM).build()
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        mediaPlayer = MediaPlayer()
        mediaPlayer?.apply {
            setAudioAttributes(audioAttrs)
            setDataSource(baseContext, defaultToneUri)
            isLooping = true
        }
        mediaPlayer?.prepare()

        mediaSession = MediaSessionCompat(this, AlarmReceiver.CHANNEL_ID)
        mediaSession?.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        sessionToken = mediaSession?.sessionToken

        playback = PlaybackStateCompat.Builder()
        playback?.setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PAUSE or PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_STOP)


        mediaSession?.setCallback(object : MediaSessionCompat.Callback(){
            override fun onPlay() {
                super.onPlay()

                startPlayback()
                mediaPlayer?.start()

                handler?.postDelayed(scheduleTimer(), 1000 * 60)
                //AudioManagerCompat.requestAudioFocus(audioManager!!, audioFocus!!)
            }

            override fun onStop() {
                super.onStop()

                mediaSession?.isActive = false
                mediaPlayer?.stop()
                handler = null
                vibrationService.cancel()
                stopForeground(true)
                stopSelf(1)
                updatePlaybackState(1)
            }
        })

        mediaController = mediaSession?.controller
        audioFocus = buildAudioRequest()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MediaButtonReceiver.handleIntent(mediaSession!!, intent)
        if (intent?.action == REMINDER_ALARM_SERVICE) {
            startForeground(1, buildNotes(intent))
            startPlayback()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        mediaSession?.isActive = false
        mediaSession?.release()
        AudioManagerCompat.abandonAudioFocusRequest(audioManager!!, audioFocus!!)
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot {
        return BrowserRoot(getString(R.string.app_name), null)
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.sendResult(mutableListOf())
    }

    private fun updatePlaybackState(state : Int){
        val currentState = when(state){
            0 -> PlaybackStateCompat.STATE_PAUSED

            1 -> PlaybackStateCompat.STATE_STOPPED

            else -> PlaybackStateCompat.STATE_PLAYING
        }

        playback?.setState(currentState, 0, 1.0F)

        mediaSession?.setPlaybackState(playback!!.build())
    }

    private fun buildAudioRequest() : AudioFocusRequestCompat {
        return AudioFocusRequestCompat.Builder(AudioManagerCompat.AUDIOFOCUS_GAIN).setOnAudioFocusChangeListener {
            if (it == AudioManagerCompat.AUDIOFOCUS_GAIN) {
                mediaPlayer?.start()
                Log.e(null, true.toString())
            }
            else {
                mediaController?.transportControls?.pause()
                Log.e(null, false.toString())
            }
        }.setWillPauseWhenDucked(false).build()
    }

    private fun buildNotes(intent: Intent?) : Notification{

        val channel = NotificationChannelCompat.Builder(AlarmReceiver.CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_HIGH).setName("Reminder").build()
        val notManager = NotificationManagerCompat.from(this)

        notManager.createNotificationChannel(channel)

        val builder = NotificationCompat.Builder(this, AlarmReceiver.CHANNEL_ID)

        intent?.let { newIntent ->

            val title = newIntent.getStringExtra(ALARM_TITLE)
            val message = newIntent.getStringExtra(ALARM_MESSAGE)
            val reqId = newIntent.getIntExtra(ALARM_ID, 0)
            val interval = newIntent.getLongExtra(ALARM_INTERVAL, 0)
            val dosage = newIntent.getIntExtra(ALARM_DOSAGE, 0)
            val dosageType = newIntent.getStringExtra(ALARM_DOSAGE_TYPE)
            val currentRingTime = newIntent.getLongExtra(ALARM_CURRENT_RING_TIME, 0)

            val fullScreenIntent = Intent(baseContext, AlarmActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra(ALARM_TITLE, title)
                putExtra(ALARM_MESSAGE, message)
                putExtra(ALARM_INTERVAL, interval)
                putExtra(ALARM_DOSAGE, dosage)
                putExtra(ALARM_DOSAGE_TYPE, dosageType)
            }
            Log.e("values", "$title $message")
            val fullScreenPendingIntent = PendingIntent.getActivity(baseContext, reqId,
                fullScreenIntent, PendingIntent.FLAG_IMMUTABLE)

            builder.setSmallIcon(R.drawable.ic_round_alarm_on_24)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setFullScreenIntent(fullScreenPendingIntent, true)

            scheduleAlarm(context = baseContext, title = title!!, message = message!!, reqId = reqId, ringTime = currentRingTime, interval = interval)
        }
        if (vibrationService.hasVibrator())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                vibrationService.vibrate(VibrationEffect.createWaveform(longArrayOf(0,500,500), 0))
            else
                vibrationService.vibrate(longArrayOf(0,500,500), 0)
        return builder.build()
    }

    private fun startPlayback(){
        mediaSession?.isActive = true
        mediaController?.transportControls?.play()
        updatePlaybackState(2)
    }

    @SuppressLint("InlinedApi")
    fun scheduleAlarm(context: Context?, title: String, message: String, reqId: Int, ringTime: Long, interval: Long){
        //remember to update firestore once you begin adding finishing touches

        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

        val newRingTime = Date(ringTime).toInstant().plus(Duration.ofHours(interval))
        val time = Date.from(newRingTime).time

        val currentTime = System.currentTimeMillis()
        if (currentTime > time) return

        val pendingIntent = Intent(context, AlarmReceiver::class.java).let {
            it.putExtra(ALARM_TITLE, title)
            it.putExtra(ALARM_MESSAGE, message)
            it.putExtra(ALARM_CURRENT_RING_TIME, time)
            it.putExtra(ALARM_INTERVAL, interval)
            it.action = ALARM_ACTION
            it.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
            PendingIntent.getBroadcast(context, reqId, it, PendingIntent.FLAG_IMMUTABLE)
        }

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            alarmManager?.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
        else
            alarmManager?.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent)*/
        val clockInfo = AlarmManager.AlarmClockInfo(time, pendingIntent)
        alarmManager?.setAlarmClock(clockInfo, pendingIntent)
    }

    private fun scheduleTimer() : Runnable{
        return Runnable {
            val playing = mediaPlayer?.isPlaying ?: false
            if(playing){
                mediaController?.transportControls?.stop()
            }
        }
    }

    companion object{
        const val REMINDER_ALARM_SERVICE = "REMINDER_ALARM_SERVICE";
    }
}