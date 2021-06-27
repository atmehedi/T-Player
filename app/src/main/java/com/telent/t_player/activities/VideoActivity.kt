package com.telent.t_player.activities

import android.content.ContentResolver
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.telent.t_player.R
import com.telent.t_player.adapter.VideoRecyclerAdapter
import com.telent.t_player.model.VideoModel

class VideoActivity : AppCompatActivity() {
    lateinit var videoRecyclerView: RecyclerView
    private var videoAr = arrayListOf<VideoModel>()
    lateinit var arraylistTitle: ArrayList<String>
    lateinit var videoName: List<String>
    lateinit var arraylistId: HashMap<String, String>
    lateinit var arraylistVideoWidth: HashMap<String, String>
    lateinit var arraylistduration: HashMap<String, String>
    lateinit var coordinator: CoordinatorLayout
    lateinit var frameLayout: FrameLayout
    lateinit var toolbar: Toolbar
    var folderId: String? = "folderId"
    var bucketTitle = "Internal Storage"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        listMain()

    }

    fun listMain() {
        if (intent != null) {
            folderId = intent.getStringExtra("content_id")
            if (folderId == null) {
                println("folder is is missing")
            }
        } else {
            println("intent is empty")
        }
        initializer()

        for (i in videoName) {
            val ttt: Int? = arraylistduration[i]?.toInt()
            val mm: Int = (ttt?.div(60000) ?: 10) % 60000
            val ss: Int = (ttt?.rem(60000) ?: 10) / 1000

            val runTime = String.format("%02d:%02d", mm, ss)


            val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString() + "/" + arraylistId[i]
            val videoWidth = arraylistVideoWidth[i]

            val vidObject = VideoModel(i, runTime, uri, videoWidth)
            videoAr.add(vidObject)

            toolBarThing()



        }

    }

    private fun toolBarThing() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = bucketTitle
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initializer() {
        videoRecyclerView = findViewById(R.id.recyclerViewVideo)
        arraylistTitle = ArrayList<String>()
        arraylistId = HashMap<String, String>()
        arraylistVideoWidth = HashMap<String, String>()
        arraylistduration = HashMap<String, String>()
        videoName = ArrayList<String>()
        coordinator = findViewById(R.id.coordinatorLayout)
        frameLayout = findViewById(R.id.frameLayout)
        toolbar = findViewById(R.id.toolBar)
        videosList()
        videoRecyclerView.layoutManager = LinearLayoutManager(this)
        videoRecyclerView.adapter = VideoRecyclerAdapter(this, videoAr)
        videoRecyclerView.setHasFixedSize(true)
    }

    fun videosList() {

        val resolver: ContentResolver = contentResolver
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val orderBy = MediaStore.Video.Media.DATE_MODIFIED

        val cursor: Cursor? = resolver.query(uri, null, null, null, "$orderBy DESC")

        when {
            cursor == null -> {
                // query failed, handle error.
                println("cursor error")
            }
            !cursor.moveToFirst() -> {
                // no media on the device
                println("no media on device")
            }
            else -> {
                val foldername = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
                val titleColumn: Int = cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)
                val idColumn: Int = cursor.getColumnIndex(MediaStore.Video.Media._ID)
                val videoWidth = cursor.getColumnIndex(MediaStore.Video.Media.WIDTH)
                val bucketColumn: Int = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID)
                val durationColumn: Int = cursor.getColumnIndex(MediaStore.Video.Media.DURATION)
                do {
                    val fName = cursor.getString(foldername)
                    val thisId = cursor.getLong(idColumn)
                    val thisTitle = cursor.getString(titleColumn)
                    val bucket = cursor.getString(bucketColumn)
                    val duration: String? = cursor.getString(durationColumn)
                    val width = cursor.getString(videoWidth)


                    if (bucket == folderId) {
                        if (duration != null) {
                            arraylistduration[thisTitle] = duration
                            arraylistId[thisTitle] = thisId.toString()
                            arraylistVideoWidth[thisTitle] = width
                            if (fName != null) {
                                bucketTitle = fName
                            }
                        }
                        arraylistTitle.add(thisTitle)

                        videoName = arraylistTitle
                    }


                } while (cursor.moveToNext())
            }
        }
        cursor?.close()


    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }



}