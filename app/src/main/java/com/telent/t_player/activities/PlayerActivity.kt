package com.telent.t_player.activities

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector.ParametersBuilder
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.telent.t_player.R
import com.telent.t_player.TrackSelectionDialog


class PlayerActivity : AppCompatActivity(),View.OnClickListener,Player.Listener {

    lateinit var playerview:PlayerView
    lateinit var player:SimpleExoPlayer
    lateinit var btnScale:ImageView
    lateinit var rotate:ImageView
    var flag = false
    var vidUri: String?="null"
    var vidName: String?="null"
    var vidWidth: String?="null"
    lateinit var videoTitle:TextView
    lateinit var trackSelector:DefaultTrackSelector
    lateinit var trackSelect:ImageView
    private var trackSelectorParameters: DefaultTrackSelector.Parameters? = null
    private var isShowingTrackSelectionDialog = false



    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UseCompatLoadingForDrawables", "InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        //fullscreen codes
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager
                .LayoutParams.FLAG_FULLSCREEN
        )
       if (intent !=null){
           vidUri = intent.getStringExtra("videoUri")
           vidName = intent.getStringExtra("videoName")
           vidWidth = intent.getStringExtra("videoWidth")

           if (vidUri==null){
               println("Uri given is null")
           }
       }else{
           println("something wrong with intent")
       }

        //track selection
if (savedInstanceState==null){
    val builder = ParametersBuilder( /* context= */this)
    trackSelectorParameters = builder.build()

}
        trackSelector = DefaultTrackSelector(this)

      player = SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector).build()
        playerview = findViewById(R.id.exoPlayerView)
        playerview.player = player
        videoTitle = playerview.findViewById(R.id.videoName)
        btnScale = playerview.findViewById(R.id.btn_fullscreen)
        rotate = playerview.findViewById(R.id.rotate)
        trackSelect = findViewById(R.id.audio_track)
        trackSelect.setOnClickListener(this)
       //println(getRotation(this))

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
       val vv = vidWidth?.toInt()//VIDEO WIDTH SIZE
        if (vv != null) {
            requestedOrientation = if (vv>width){
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            }else{
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
        var clickCount = 0
        btnScale.setOnClickListener {
            val view: View = LayoutInflater.from(this).inflate(R.layout.custom_toast, null)
            val text = view.findViewById<TextView>(R.id.custom_toast_text)
            when (clickCount) {
                0 -> {
                    playerview.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    val toast = Toast.makeText(this, "Center crop", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.view = view
                    text.text = getString(R.string.zoom)
                    toast.show()
                    btnScale.setImageResource(R.drawable.ic_baseline_zoom_out_map_24)

                }
                1 -> {
                    playerview.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                    val toast = Toast.makeText(this, "Center crop", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.view = view
                    text.text = getString(R.string.fillToScreen)
                    toast.show()
                    btnScale.setImageResource(R.drawable.ic_baseline_fullscreen_24)
                }
                2 -> {
                    playerview.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
                    playerview.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
                    val toast = Toast.makeText(this, "Center crop", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.view = view
                    text.text = getString(R.string.stretched)
                    toast.show()
                    btnScale.setImageResource(R.drawable.ic_baseline_open_with_24)

                }
                3 -> {
                    playerview.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
                    val toast = Toast.makeText(this, "Center crop", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.view = view
                    text.text = getString(R.string.hundred)
                    toast.show()
                    btnScale.setImageResource(R.drawable.ic_baseline_height_24)

                }
                4 -> {
                    playerview.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
                    playerview.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
                    val toast = Toast.makeText(this, "Center crop", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.view = view
                    text.text = getString(R.string.width)
                    toast.show()
                    btnScale.setImageResource(R.drawable.ic_baseline_switch_video_24)
                }
                else -> {
                    clickCount=-1
                }
            }
        clickCount++
        }
        rotate.setOnClickListener {

          if (flag) {
              requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
              flag = false
          } else {
              requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
              flag = true

          }
      }
        player.setMediaItem(MediaItem.fromUri(vidUri.toString()))
        playUndisterbed()

        videoTitle.text = vidName
        player.prepare()
        player.playWhenReady
        player.volume
        player.play()

    }

    private fun playUndisterbed() {
        val audioAttributes: AudioAttributes = AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MOVIE)
                .build()
        player.setAudioAttributes(audioAttributes,true)
    }

    override fun onPause() {
        super.onPause()
        player.playWhenReady = false
        player.playbackState
        player.release()
    }

    override fun onRestart() {
        super.onRestart()
        player.playWhenReady = true
        player.playbackState
    }
  override fun onClick(v: View?) {
        if (v === trackSelect && !isShowingTrackSelectionDialog
                && TrackSelectionDialog.willHaveContent(trackSelector)) {
            isShowingTrackSelectionDialog = true
            val trackSelectionDialog = TrackSelectionDialog.createForTrackSelector(
                    trackSelector  /* onDismissListener= */
            ) { dismissedDialog -> isShowingTrackSelectionDialog = false }
            trackSelectionDialog.show(supportFragmentManager,  /* tag= */null)
        }
    }
    override fun onTimelineChanged(timeline: Timeline, manifest: Any?, reason: Int) {

    }

    override fun onTracksChanged(trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {

    }

    override fun onLoadingChanged(isLoading: Boolean) {

    }


    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        //buffering check
        if (playbackState == Player.STATE_BUFFERING){
            println("Buffering...")
        }else if(playbackState==Player.STATE_READY){
           println("Ready!")
        }
    }

    override fun onRepeatModeChanged(repeatMode: Int) {

    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {

    }

    override fun onPlayerError(error: ExoPlaybackException) {

    }

    override fun onPositionDiscontinuity(reason: Int) {

    }

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {

    }

    override fun onSeekProcessed() {

    }


}

