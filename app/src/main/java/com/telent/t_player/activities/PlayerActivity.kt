package com.telent.t_player.activities

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.Point
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.OpenableColumns
import android.util.DisplayMetrics
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
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
    private var deviceVolume = 0

    private var vol = 1
    private var lockMode = false
    private lateinit var mGestureDetector: GestureDetector
   private lateinit var brightLevel:TextView
   private lateinit var infoLayout:RelativeLayout
    private lateinit var levelIcon:ImageView

    private lateinit var rewLayout:LinearLayout
    private lateinit var forLayout:LinearLayout

   private lateinit var centerText:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_player)




        this.upData = this.getSharedPreferences(this.getString(R.string.Mehedi), MODE_PRIVATE)
        this.audioManager = this.getSystemService(AUDIO_SERVICE) as AudioManager

        val display = this.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        this.sWidth = size.x
        this.sHeight = size.y

        this.window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                this.window.decorView.apply {
                    this.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

                    //Hide navigation while not touching
                }
            }
        }

        val data: Uri? = this.intent?.data
        if (this.intent?.type?.startsWith("video/") == true) {
            println("video found")

        }

        val returnCursor = data?.let { this.contentResolver.query(it, null, null, null, null) }
        val nameIndex = returnCursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor?.moveToFirst()
        val fileName = nameIndex?.let { returnCursor.getString(it) }
        returnCursor?.close()

        this.sharedPreferences = this.getSharedPreferences(this.getString(R.string.shared_value_file), Context.MODE_PRIVATE)
        this.focusCheck = this.getSharedPreferences(this.getString(R.string.shared_value_focus), Context.MODE_PRIVATE)

        //fullscreen codes
        this.window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager
                .LayoutParams.FLAG_FULLSCREEN
        )
        if (this.intent != null) {
            this.vidUri = this.intent.getStringExtra("videoUri")
            this.vidName = this.intent.getStringExtra("videoName")
            this.vidWidth = this.intent.getStringExtra("videoWidth")
            this.position = this.intent.getStringExtra("position")


            if (this.vidUri == null) {
                println("Uri given is null")
            }
        } else {
            println("something wrong with intent")
        }


        //track selection
        if (savedInstanceState == null) {
            val builder = ParametersBuilder( /* context= */this)
            this.trackSelectorParameters = builder.build()

        }
        this.trackSelector = DefaultTrackSelector(this) //Audio track selector

        val loadControl = DefaultLoadControl()
        val renderersFactory = DefaultRenderersFactory(this)
                .setEnableDecoderFallback(true).setExtensionRendererMode(EXTENSION_RENDERER_MODE_PREFER) //Sw decoder



       // val renderersFactory = DefaultRenderersFactory(this)


        this.player = SimpleExoPlayer.Builder(this, renderersFactory)
                .setTrackSelector(this.trackSelector)
                .setLoadControl(loadControl).build()


        this.initializer()


            this.deviceVolume = this.player.deviceVolume




        this.mGestureDetector = GestureDetector(this, this)

        this.lock.setOnClickListener(this)
        this.totalLayout.setOnTouchListener(this)

        this.trackSelect.setOnClickListener(this)
        this.back.setOnClickListener(this)

        val displayMetrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val vv = this.vidWidth?.toInt()//VIDEO WIDTH SIZE

        if (vv != null) {
            this.requestedOrientation = if (vv > width) {
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            } else {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
        var clickCount = 0
        this.btnScale.setOnClickListener {
            when (clickCount) {
                0 -> {
                    this.playerview.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    this.centerText.visibility = View.VISIBLE
                    this.centerText.text = this.getString(R.string.zoom)
                    object : CountDownTimer(500, 1000) {
                        override fun onTick(millisUntilFinished: Long) {//running functionality for now its no use
                        }

                        override fun onFinish() {
                            this@PlayerActivity.centerText.visibility = View.GONE
                        }
                    }.start()
                    this.btnScale.setImageResource(R.drawable.ic_baseline_zoom_out_map_24)


                }
                1 -> {
                    this.playerview.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                    this.centerText.visibility = View.VISIBLE
                    this.centerText.text = this.getString(R.string.fillToScreen)
                    object : CountDownTimer(500, 1000) {
                        override fun onTick(millisUntilFinished: Long) {//running functionality for now its no use
                        }

                        override fun onFinish() {
                            this@PlayerActivity.centerText.visibility = View.GONE
                        }
                    }.start()
                    this.btnScale.setImageResource(R.drawable.ic_baseline_fullscreen_24)
                }
                2 -> {
                    this.playerview.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
                    this.playerview.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
                    this.centerText.visibility = View.VISIBLE
                    this.centerText.text = this.getString(R.string.stretched)
                    object : CountDownTimer(500, 1000) {
                        override fun onTick(millisUntilFinished: Long) {//running functionality for now its no use
                        }

                        override fun onFinish() {
                            this@PlayerActivity.centerText.visibility = View.GONE
                        }
                    }.start()
                    this.btnScale.setImageResource(R.drawable.ic_baseline_open_with_24)

                }
                3 -> {
                    this.playerview.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
                    this.centerText.visibility = View.VISIBLE
                    this.centerText.text = this.getString(R.string.hundred)
                    object : CountDownTimer(500, 1000) {
                        override fun onTick(millisUntilFinished: Long) {//running functionality for now its no use
                        }

                        override fun onFinish() {
                            this@PlayerActivity.centerText.visibility = View.GONE
                        }
                    }.start()
                    this.btnScale.setImageResource(R.drawable.ic_baseline_height_24)

                }
                4 -> {
                    this.playerview.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
                    this.playerview.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
                    this.centerText.visibility = View.VISIBLE
                    this.centerText.text = this.getString(R.string.width)
                    object : CountDownTimer(500, 1000) {
                        override fun onTick(millisUntilFinished: Long) {//running functionality for now its no use
                        }

                        override fun onFinish() {
                            this@PlayerActivity.centerText.visibility = View.GONE
                        }
                    }.start()
                    this.btnScale.setImageResource(R.drawable.ic_baseline_switch_video_24)
                }
                else -> {
                    clickCount = -1
                }
            }
            clickCount++
        }
        this.rotate.setOnClickListener {

            if (this.flag) {
                this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                this.flag = false
            } else {
                this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                this.flag = true

            }
        }
        if (data != null) {
            this.vidUri = data.toString()
        }
        this.player.setMediaItem(MediaItem.fromUri(this.vidUri.toString()))

        if (this.position != null) {
            this.player.seekTo(this.position!!.toLong())
        }
        val focusMode = this.focusCheck.getString("checked", "Unchecked")
        if (focusMode == "checked") {
            this.playUndisterbed()
        }

        if (fileName !== null) {
            this.vidName = fileName
        }



        this.videoTitle.text = this.vidName
        this.player.prepare()
        this.player.playWhenReady
        this.player.volume
        this.player.play()
        println(player.audioFormat)

    }

    private fun initializer() {                              //I N I T I A L I Z A R   C H E C K P O I N T
        this.playerview = this.findViewById(R.id.exoPlayerView)
        this.playerview.player = this.player
        this.videoTitle = this.playerview.findViewById(R.id.videoName)

        this.btnScale = this.playerview.findViewById(R.id.btn_fullscreen)
        this.rotate = this.playerview.findViewById(R.id.rotate)
        this.trackSelect = this.findViewById(R.id.audio_track)

        this.back = this.playerview.findViewById(R.id.back)
        this.lock = this.playerview.findViewById(R.id.lock)
        this.soundsBar = this.playerview.findViewById(R.id.progressBar2)
        this.brightnessBar = this.playerview.findViewById(R.id.progressBar1)

        this.controls = this.playerview.findViewById(R.id.root)
        this.totalLayout = this.playerview.findViewById(R.id.totalLayout)

        this.brightLevel = this.playerview.findViewById(R.id.txtInfo)
        this.infoLayout = this.playerview.findViewById(R.id.info)
        this.levelIcon = this.playerview.findViewById(R.id.brightnessIcon)

        this.rewLayout = this.playerview.findViewById(R.id.rewLayout)
        this.forLayout = this.playerview.findViewById(R.id.forLayout)

        this.centerText = this.playerview.findViewById(R.id.centerText)

    }


    private fun playUndisterbed() {    //play alone system
        val audioAttributes: AudioAttributes = AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MOVIE)
                .build()
        this.player.setAudioAttributes(audioAttributes, true)
    }

    override fun onPause() {                             //O V E R R I D E F U N C T I O N    C H E C K P O I N T
        super.onPause()
        this.player.pause()


    }

    override fun onDestroy() {
        this.upData.edit().clear().apply()
        this.player.playWhenReady = false
        this.player.playbackState
        this.player.release()
        super.onDestroy()

    }

    override fun onRestart() {
        super.onRestart()
        this.player.playWhenReady = true
        this.player.playbackState

    }

    override fun onResume() {
        this.player.playWhenReady = true
        //player.play()
        super.onResume()

    }

    override fun onClick(v: View?) {                             //O N C L I C K   F U N C T I O N   C H E C K P O I N T
        if (v === this.trackSelect && !this.isShowingTrackSelectionDialog
                && TrackSelectionDialog.willHaveContent(this.trackSelector)) {
            this.isShowingTrackSelectionDialog = true
            val trackSelectionDialog = TrackSelectionDialog.createForTrackSelector(
                    this.trackSelector  /* onDismissListener= */
            ) { this.isShowingTrackSelectionDialog = false }
            trackSelectionDialog.show(this.supportFragmentManager,  /* tag= */null)
        }
        if (v == this.back) {
            this.pref()
            this.finish()
        }
        if (v == this.lock) {
            if (this.controls.visibility == View.VISIBLE) {
                this.lockMode = true
                this.lock.setImageResource(R.drawable.ic_baseline_lock_24)
                this.lock.visibility = View.VISIBLE
                this.controls.visibility = View.GONE
            } else if (this.controls.visibility == View.GONE) {
                this.lockMode = false
                this.lock.setImageResource(R.drawable.ic_baseline_lock_open_24)
                this.controls.visibility = View.VISIBLE
            }
        }
    }

    override fun onBackPressed() {
        this.pref()
        super.onBackPressed()
    }

    private fun pref() {
        this.sharedPreferences.edit().putString("Uri", this.vidUri).apply()
        this.sharedPreferences.edit().putString("videoName", this.vidName).apply()
        this.sharedPreferences.edit().putString("width", this.vidWidth).apply()
        this.sharedPreferences.edit().putString("position", this.player.currentPosition.toString()).apply()
    }
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {                   //O N T O U C H   C H E C K P O I N T
        this.mGestureDetector.onTouchEvent(event)
        return when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                // We pressed on the left
                if (event.x < (this.sWidth / 2)) {
                    this.leftClicked = true
                    // We pressed on the right
                } else if (event.x > (this.sWidth / 2)) {

                    this.rightClicked = true
                }
                this.basex = event.x
                this.basey = event.y
                true
            }
            MotionEvent.ACTION_MOVE -> {

                this.diffX = ceil((event.x - this.basex).toDouble()).toFloat()
                this.diffY = ceil((this.basey - event.y).toDouble()).toFloat()

                if (this.rightClicked) {
                    this.prevBrightness = this.upData.getString("brightness", "0.01f")
                    if (this.diffY != 0.0f && this.diffX < 20) {
                        this.brightness = this.prevBrightness!!.toFloat() + this.diffY / 500
                        this.pBright = this.brightness * 100
                        if (this.brightness > 1) {
                            this.brightness = 1.0f
                        } else if (this.brightness < 0.01f) {
                            this.brightness = 0.01f
                        }
                        this.brightnessBar.visibility = View.VISIBLE
                        val layout = this.window.attributes
                        layout.screenBrightness
                        layout.screenBrightness = this.brightness
                        this.window.attributes = layout
                        this.oldBrightness = layout.screenBrightness
                        this.brightnessBar.progress = this.pBright.toInt()

                        //for text
                        this.infoLayout.visibility = View.VISIBLE
                        val textValueB = this.brightness * 10
                        this.brightLevel.text = textValueB.toInt().toString()
                        this.levelIcon.setImageResource(R.drawable.ic_baseline_brightness_medium_24)


                    }
                } else if (this.leftClicked) {


                    if (this.diffY != 0.0f && this.diffX < 20) {
                        val prevVolume = this.upData.getInt("volume", this.deviceVolume)
                        this.soundsBar.visibility = View.VISIBLE

                        this.vol = this.deviceVolume + (this.diffY / 30).toInt()
                        println("dv = ${this.deviceVolume} , v= ${this.vol} ,prev = $prevVolume and ${this.diffY}")
                        if (this.vol > 30) {

                            this.vol = 30

                        } else if (this.vol < 0) {

                            this.vol = 0

                        }
                        this.player.deviceVolume = this.vol
                        this.soundsBar.progress = (this.vol * 3.34).toInt()
                        //for text
                        this.infoLayout.visibility = View.VISIBLE
                        val textValueB = this.vol
                        this.brightLevel.text = textValueB.toString()
                        this.levelIcon.setImageResource(R.drawable.ic_baseline_volume_up_24)

                    }
                }

                true
            }
            MotionEvent.ACTION_UP -> {                                  // O N  F I N G E R  U P
                this.rightClicked = false
                this.leftClicked = false
                this.deviceVolume = this.player.deviceVolume

                this.brightnessBar.visibility = View.GONE
                this.soundsBar.visibility = View.GONE
                this.brightnessBar.progress = this.brightness.toInt()
                this.soundsBar.progress = this.deviceVolume

                this.upData.edit().putString("brightness", this.brightness.toString()).apply()
                this.upData.edit().putInt("volume", this.vol).apply()

                this.infoLayout.visibility = View.GONE

                true
            }
            MotionEvent.ACTION_CANCEL -> {
                //code for action cancel
                true
            }
            MotionEvent.ACTION_OUTSIDE -> {
                //code for action outside
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

        if (this.controls.visibility == View.VISIBLE && !this.lockMode) {
            this.controls.visibility = View.GONE
            this.lock.visibility = View.GONE

        } else if (this.controls.visibility == View.GONE && !this.lockMode) {
            this.controls.visibility = View.VISIBLE
            this.lock.visibility = View.VISIBLE
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
        val currentPosition = this.player.currentPosition
        if (e!!.x < (this.sWidth / 2)) {
           this.rewLayout.visibility = View.VISIBLE
            object : CountDownTimer(500, 1000) {
                override fun onTick(millisUntilFinished: Long) {//running functionality for now its no use
                }
                override fun onFinish() {
                    this@PlayerActivity.rewLayout.visibility = View.GONE
                }
            }.start()


            this.player.seekTo(currentPosition - 30000)

            // We pressed on the right
        } else if (e.x > (this.sWidth / 2)) {
            this.forLayout.visibility = View.VISIBLE
            object : CountDownTimer(500, 1000) {
                override fun onTick(millisUntilFinished: Long) {//running functionality for now its no use
                }
                override fun onFinish() {
                    this@PlayerActivity.forLayout.visibility = View.GONE
                }
            }.start()

            this.player.seekTo(currentPosition + 30000)

        }

        return true
    }

    override fun onDoubleTapEvent(e: MotionEvent?): Boolean {

        return false
    }

    override fun onStart() {
        super.onStart()

        val decorView: View = this.window.decorView

        val uiOptions: Int = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
        decorView.systemUiVisibility = uiOptions

    }


}

