package com.telent.t_player.activities

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.media.MediaCodecInfo
import android.media.MediaCodecList
import android.os.Build
import android.os.Bundle
import android.os.Handler
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
import com.google.android.exoplayer2.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON
import com.google.android.exoplayer2.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector.ParametersBuilder
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.BuildConfig
import com.google.android.exoplayer2.ui.PlayerView
import com.telent.t_player.R
import com.telent.t_player.exo_content.TrackSelectionDialog


class PlayerActivity : AppCompatActivity(),View.OnClickListener,Player.Listener {

    lateinit var playerview:PlayerView
    lateinit var player:SimpleExoPlayer
    lateinit var btnScale:ImageView
    lateinit var rotate:ImageView
    lateinit var back:ImageView
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
        trackSelector = DefaultTrackSelector(this) //Audio track selector

        val loadControl = DefaultLoadControl()
       val renderersFactory = DefaultRenderersFactory(this)


        player = SimpleExoPlayer.Builder(this,renderersFactory)
                .setTrackSelector(trackSelector)
                .setLoadControl(loadControl).build()

        loadControl.shouldStartPlayback(player.currentPosition,1.0f,true,C.TIME_UNSET)



        playerview = findViewById(R.id.exoPlayerView)
        playerview.player = player
        videoTitle = playerview.findViewById(R.id.videoName)
        btnScale = playerview.findViewById(R.id.btn_fullscreen)
        rotate = playerview.findViewById(R.id.rotate)
        trackSelect = findViewById(R.id.audio_track)
        back = playerview.findViewById(R.id.back)
        trackSelect.setOnClickListener(this)
        back.setOnClickListener(this)
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

    private fun playUndisterbed() {    //play alone system
        val audioAttributes: AudioAttributes = AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MOVIE)
                .build()
        player.setAudioAttributes(audioAttributes, true)
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
      if (v==back){
          finish()
      }
    }

}

