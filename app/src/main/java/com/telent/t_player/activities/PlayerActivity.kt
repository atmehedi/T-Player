package com.telent.t_player.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
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
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector.ParametersBuilder
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.telent.t_player.R
import com.telent.t_player.exo_content.TrackSelectionDialog


class PlayerActivity : AppCompatActivity(),View.OnClickListener,Player.Listener {

    private var flag = false
    private var vidUri: String?="null"
    private var vidName: String?="null"
    private var vidWidth: String?="null"
    private var position: String?="null"
    private lateinit var playerview:PlayerView
    private lateinit var player:SimpleExoPlayer
    private lateinit var btnScale:ImageView
    private lateinit var rotate:ImageView
    private lateinit var back:ImageView
    private lateinit var videoTitle:TextView
    private lateinit var trackSelector:DefaultTrackSelector
    private lateinit var trackSelect:ImageView
    private lateinit var focusCheck:SharedPreferences
    private lateinit var sharedPreferences: SharedPreferences
    private var trackSelectorParameters: DefaultTrackSelector.Parameters? = null
    private var isShowingTrackSelectionDialog = false



    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UseCompatLoadingForDrawables", "InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
          window.decorView.apply {
              systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
              //Hide navigation while not touching
          }
            }
        }

        val data: Uri? = intent?.data
        if (intent?.type?.startsWith("video/")==true){
            println("video found")


        }

            val returnCursor = data?.let { contentResolver.query(it, null, null, null, null) }
            val nameIndex = returnCursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor?.moveToFirst()
            val fileName = nameIndex?.let { returnCursor.getString(it) }
             println(fileName)
            returnCursor?.close()





        sharedPreferences = getSharedPreferences(getString(R.string.shared_value_file), Context.MODE_PRIVATE)
        focusCheck = getSharedPreferences(getString(R.string.shared_value_focus),Context.MODE_PRIVATE)

        //fullscreen codes
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager
                .LayoutParams.FLAG_FULLSCREEN
        )
       if (intent !=null){
           vidUri = intent.getStringExtra("videoUri")
           vidName = intent.getStringExtra("videoName")
           vidWidth = intent.getStringExtra("videoWidth")
           position = intent.getStringExtra("position")


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

        if (data!=null){
            vidUri = data.toString()
        }
        player.setMediaItem(MediaItem.fromUri(vidUri.toString()))

        if (position !=null){
            player.seekTo(position!!.toLong())
        }
        val focusMode = focusCheck.getString("checked","Unchecked")
        if (focusMode =="checked"){
            playUndisterbed()
        }

        if (fileName!==null){
            vidName = fileName
        }

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
     player.pause()


    }

    override fun onDestroy() {
        player.playWhenReady = false
        player.playbackState
        player.release()
        super.onDestroy()

    }

    override fun onRestart() {
        super.onRestart()
        player.playWhenReady = true
        player.playbackState

    }

    override fun onResume() {
        player.playWhenReady=true
        //player.play()
        super.onResume()

    }
  override fun onClick(v: View?) {
        if (v === trackSelect && !isShowingTrackSelectionDialog
                && TrackSelectionDialog.willHaveContent(trackSelector)) {
            isShowingTrackSelectionDialog = true
            val trackSelectionDialog = TrackSelectionDialog.createForTrackSelector(
                    trackSelector  /* onDismissListener= */
            ) { isShowingTrackSelectionDialog = false }
            trackSelectionDialog.show(supportFragmentManager,  /* tag= */null)
        }
      if (v==back){
          pref()
          finish()
      }
    }

    override fun onBackPressed() {
       pref()
        super.onBackPressed()
    }

    private fun pref() {
       val uri1 = sharedPreferences.getString("Uri",null)
        val position = sharedPreferences.getString("position","100")
        println("$uri1 and $position")

        sharedPreferences.edit().putString("Uri",vidUri).apply()
        sharedPreferences.edit().putString("videoName",vidName).apply()
        sharedPreferences.edit().putString("width",vidWidth).apply()
        sharedPreferences.edit().putString("position", player.currentPosition.toString()).apply()
    }
}

