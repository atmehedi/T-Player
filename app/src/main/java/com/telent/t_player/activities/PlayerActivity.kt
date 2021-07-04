package com.telent.t_player.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.Point
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector.ParametersBuilder
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.telent.t_player.R
import com.telent.t_player.exo_content.TrackSelectionDialog
import kotlin.math.ceil


class PlayerActivity : AppCompatActivity(), View.OnClickListener, Player.Listener, View.OnTouchListener, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private var flag = false
    private var vidUri: String? = "null"
    private var vidName: String? = "null"
    private var vidWidth: String? = "null"
    private var position: String? = "null"
    private var prevBrightness: String? = "0.01f"
    private var prevVolume: Int? = 0

    private lateinit var playerview: PlayerView
    private lateinit var player: SimpleExoPlayer
    private lateinit var btnScale: ImageView
    private lateinit var rotate: ImageView
    private lateinit var back: ImageView
    private lateinit var videoTitle: TextView
    private lateinit var trackSelector: DefaultTrackSelector
    private lateinit var trackSelect: ImageView
    private lateinit var focusCheck: SharedPreferences
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var upData: SharedPreferences

    private var trackSelectorParameters: DefaultTrackSelector.Parameters? = null
    private var isShowingTrackSelectionDialog = false
    private lateinit var lock: ImageView
    private lateinit var controls: RelativeLayout
    private lateinit var brightnessBar: ProgressBar
    private lateinit var soundsBar: ProgressBar
    private var DEBUG_TAG = "Gestures"
    private lateinit var totalLayout: RelativeLayout
    private var basex = 0f
    private var basey = 0f
    private var diffX = 0f
    private var diffY = 0f
    private var sWidth = 0
    private var sHeight: Int = 0
    private var leftClicked = false
    private var rightClicked = false
    private var brightness = 0.0f
    private var pBright = 0.0f
    private var oldBrightness = 0.0f
    private lateinit var audioManager: AudioManager
    private var vol = 1
    private var lockMode = false
    lateinit var mGestureDetector: GestureDetector


    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        upData = getSharedPreferences(getString(R.string.Mehedi), MODE_PRIVATE)
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        sWidth = size.x
        sHeight = size.y

        window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                window.decorView.apply {
                    systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

                    //Hide navigation while not touching
                }
            }
        }

        val data: Uri? = intent?.data
        if (intent?.type?.startsWith("video/") == true) {
            println("video found")


        }

        val returnCursor = data?.let { contentResolver.query(it, null, null, null, null) }
        val nameIndex = returnCursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor?.moveToFirst()
        val fileName = nameIndex?.let { returnCursor.getString(it) }
        returnCursor?.close()





        sharedPreferences = getSharedPreferences(getString(R.string.shared_value_file), Context.MODE_PRIVATE)
        focusCheck = getSharedPreferences(getString(R.string.shared_value_focus), Context.MODE_PRIVATE)

        //fullscreen codes
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager
                .LayoutParams.FLAG_FULLSCREEN
        )
        if (intent != null) {
            vidUri = intent.getStringExtra("videoUri")
            vidName = intent.getStringExtra("videoName")
            vidWidth = intent.getStringExtra("videoWidth")
            position = intent.getStringExtra("position")


            if (vidUri == null) {
                println("Uri given is null")
            }
        } else {
            println("something wrong with intent")
        }


        //track selection
        if (savedInstanceState == null) {
            val builder = ParametersBuilder( /* context= */this)
            trackSelectorParameters = builder.build()

        }
        trackSelector = DefaultTrackSelector(this) //Audio track selector

        val loadControl = DefaultLoadControl()
        val renderersFactory = DefaultRenderersFactory(this)


        player = SimpleExoPlayer.Builder(this, renderersFactory)
                .setTrackSelector(trackSelector)
                .setLoadControl(loadControl).build()


        playerview = findViewById(R.id.exoPlayerView)
        playerview.player = player
        videoTitle = playerview.findViewById(R.id.videoName)
        btnScale = playerview.findViewById(R.id.btn_fullscreen)
        rotate = playerview.findViewById(R.id.rotate)
        trackSelect = findViewById(R.id.audio_track)
        back = playerview.findViewById(R.id.back)
        lock = playerview.findViewById(R.id.lock)
        soundsBar = playerview.findViewById(R.id.progressBar2)

        controls = playerview.findViewById(R.id.root)
        brightnessBar = playerview.findViewById(R.id.progressBar1)
        totalLayout = playerview.findViewById(R.id.totalLayout)

        mGestureDetector = GestureDetector(this, this)



        lock.setOnClickListener(this)
        totalLayout.setOnTouchListener(this)

        trackSelect.setOnClickListener(this)
        back.setOnClickListener(this)


        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val vv = vidWidth?.toInt()//VIDEO WIDTH SIZE

        if (vv != null) {
            requestedOrientation = if (vv > width) {
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            } else {
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
                    clickCount = -1
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
        if (data != null) {
            vidUri = data.toString()
        }
        player.setMediaItem(MediaItem.fromUri(vidUri.toString()))

        if (position != null) {
            player.seekTo(position!!.toLong())
        }
        val focusMode = focusCheck.getString("checked", "Unchecked")
        if (focusMode == "checked") {
            playUndisterbed()
        }

        if (fileName !== null) {
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
        player.playWhenReady = true
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
        if (v == back) {
            pref()
            finish()
        }
        if (v == lock) {
            if (controls.visibility == View.VISIBLE) {
                lockMode = true
                lock.setImageResource(R.drawable.ic_baseline_lock_24)
                lock.visibility = View.VISIBLE
                controls.visibility = View.GONE
            } else if (controls.visibility == View.GONE) {
                lockMode = false
                lock.setImageResource(R.drawable.ic_baseline_lock_open_24)
                controls.visibility = View.VISIBLE
            }


        }


    }

    override fun onBackPressed() {
        pref()
        super.onBackPressed()
    }

    private fun pref() {

        sharedPreferences.edit().putString("Uri", vidUri).apply()
        sharedPreferences.edit().putString("videoName", vidName).apply()
        sharedPreferences.edit().putString("width", vidWidth).apply()
        sharedPreferences.edit().putString("position", player.currentPosition.toString()).apply()
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {

        mGestureDetector.onTouchEvent(event)
        return when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                // We pressed on the left
                if (event.x < (sWidth / 2)) {
                    leftClicked = true

                    // We pressed on the right
                } else if (event.x > (sWidth / 2)) {

                    rightClicked = true
                }
                basex = event.x
                basey = event.y
                true
            }
            MotionEvent.ACTION_MOVE -> {

                diffX = ceil((event.x - basex).toDouble()).toFloat()
                diffY = ceil((basey - event.y).toDouble()).toFloat()

                if (rightClicked) {
                    prevBrightness = upData.getString("brightness", "0.01f")
                    if (diffY != 0.0f && diffX < 20) {
                        brightness = prevBrightness!!.toFloat() + diffY / 500
                        pBright = brightness * 100
                        if (brightness > 1) {
                            brightness = 1.0f
                        } else if (brightness < 0.01f) {
                            brightness = 0.01f
                        }
                        brightnessBar.visibility = View.VISIBLE
                        val layout = window.attributes
                        layout.screenBrightness
                        layout.screenBrightness = brightness
                        window.attributes = layout
                        oldBrightness = layout.screenBrightness
                        brightnessBar.progress = pBright.toInt()
                    }
                } else if (leftClicked) {
                    prevVolume = upData.getInt("volume", 1)

                    if (diffY != 0.0f && diffX < 20) {

                        soundsBar.visibility = View.VISIBLE
                        vol = prevVolume!! + (diffY / 30).toInt()

                        if (vol > 30) {

                            vol = 30

                        } else if (vol < 0) {

                            vol = 0

                        }
                            player.deviceVolume = vol
                            soundsBar.progress = (vol * 3.34).toInt()
                    }
                }

                 true
            }
            MotionEvent.ACTION_UP -> {

                    rightClicked = false
                    leftClicked = false

                    brightnessBar.visibility = View.GONE
                    soundsBar.visibility = View.GONE
                    brightnessBar.progress = brightness.toInt()
                    soundsBar.progress = prevVolume!!

                upData.edit().putString("brightness", brightness.toString()).apply()
                upData.edit().putInt("volume", vol).apply()

                true
            }
            MotionEvent.ACTION_CANCEL -> {
                Log.d(DEBUG_TAG, "Action was CANCEL")
                true
            }
            MotionEvent.ACTION_OUTSIDE -> {
                Log.d(DEBUG_TAG, "Movement occurred outside bounds of current screen element")
                true
            }
            else -> super.onTouchEvent(event)

        }


    }

    override fun onDown(e: MotionEvent?): Boolean {
        return true
    }

    override fun onShowPress(e: MotionEvent?) {

    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {

        if (controls.visibility == View.VISIBLE && !lockMode) {
            controls.visibility = View.GONE
            lock.visibility = View.GONE

        } else if (controls.visibility == View.GONE && !lockMode) {
            controls.visibility = View.VISIBLE
            lock.visibility = View.VISIBLE
        }

        return true
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        return true
    }

    override fun onLongPress(e: MotionEvent?) {

    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        return true
    }

    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        return false
    }

    override fun onDoubleTap(e: MotionEvent?): Boolean {
        val currentPosition = player.currentPosition
        if (e!!.x < (sWidth / 2)) {


            player.seekTo(currentPosition - 10000)

            // We pressed on the right
        } else if (e.x > (sWidth / 2)) {

            player.seekTo(currentPosition + 10000)

        }

        return true
    }

    override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
        return false
    }


}

