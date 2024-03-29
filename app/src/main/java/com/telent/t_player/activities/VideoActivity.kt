package com.telent.t_player.activities

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.telent.t_player.R
import com.telent.t_player.adapter.VideoRecyclerAdapter
import com.telent.t_player.model.VideoModel

class VideoActivity : AppCompatActivity() {
    private lateinit var videoRecyclerView: RecyclerView
    private var videoAr = arrayListOf<VideoModel>()
    private lateinit var arraylistTitle: ArrayList<String>
    private lateinit var videoName: List<String>
    private lateinit var arraylistId: HashMap<String, String>
    private lateinit var arrayFolderName: HashMap<String, String>
    private lateinit var arraylistVideoWidth: HashMap<String, String>
    private lateinit var arraylistDuration: HashMap<String, String>
    private lateinit var coordinator: CoordinatorLayout
    private lateinit var frameLayout: FrameLayout
    private lateinit var toolbar: Toolbar
    private lateinit var fab2: FloatingActionButton
    private var folderId: String? = "folderId"
    private var bucketTitle = "Internal Storage"
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var refresher: SwipeRefreshLayout

    private lateinit var arrayListDate: HashMap<String, String>
    private lateinit var arrayListDateModified: HashMap<String, String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        sharedPreferences = getSharedPreferences(
            getString(R.string.shared_value_file),
            Context.MODE_PRIVATE
        )
        refresher = findViewById(R.id.refresher)

        listMain()
        refresher.setOnRefreshListener {

            videoAr.clear()
            listMain()
            refresher.isRefreshing = false
        }
    }

    private fun listMain() {
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
            val ttt: Int? = arraylistDuration[i]?.toInt()
            val mm: Int = (ttt?.div(60000) ?: 10) % 60000
            val ss: Int = (ttt?.rem(60000) ?: 10) / 1000

            val runTime = String.format("%02d:%02d", mm, ss)


            val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString() + "/" + arraylistId[i]
            val videoWidth = arraylistVideoWidth[i]

            val vidObject = VideoModel(
                i,
                runTime,
                uri,
                videoWidth,
                arrayListDate[i],
                arrayListDateModified[i],
                arrayFolderName[i]
            )
            videoAr.add(vidObject)

            toolBarThing()


        }
        sortingFunction()

    }

    private fun sortingFunction() {     //APPLYING SORT SELECTION

        val sortValue = sharedPreferences.getString("sortValue", "byName")
        val sortType = sharedPreferences.getString("sortType", "byName")

        when (sortValue) {
            "Sort By date" -> {
                videoAr.sortWith(compareBy { it.dateAdded })
            }

            "Sort By Last modified" -> {
                videoAr.sortWith(compareBy { it.dateModified })
            }

            "Sort By name" -> {
                videoAr.sortWith(compareBy { it.resName })
            }

            else -> {
                videoAr.sortWith(compareBy { it.resName })
            }
        }
        if (sortType == "Descending") {
            videoAr.reverse()
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
        arraylistTitle = ArrayList()
        arraylistId = HashMap()
        arrayFolderName = HashMap()
        arraylistVideoWidth = HashMap()
        arraylistDuration = HashMap()
        videoName = ArrayList()
        arrayListDate = HashMap()
        arrayListDateModified = HashMap()
        coordinator = findViewById(R.id.coordinatorLayout)
        frameLayout = findViewById(R.id.frameLayout)
        toolbar = findViewById(R.id.toolBar)
        fab2 = findViewById(R.id.fab2)
        videosList()
        videoRecyclerView.layoutManager = LinearLayoutManager(this)
        videoRecyclerView.adapter = VideoRecyclerAdapter(this, videoAr)
        videoRecyclerView.setHasFixedSize(true)


    }

    private fun videosList() {
        val check = sharedPreferences.getString("Uri", null)
        if (check == null) {
            fab2.visibility = View.GONE
        }
        fab2.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            val uri2 = sharedPreferences.getString("Uri", null)
            val videoName = sharedPreferences.getString("videoName", "Video Name.mp4")
            val videoWidth = sharedPreferences.getString("width", null)
            val position = sharedPreferences.getString("position", null)
            intent.putExtra("videoUri", uri2)
            intent.putExtra("videoName", videoName)
            intent.putExtra("videoWidth", videoWidth)
            intent.putExtra("position", position)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }


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
                val folderName = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
                val titleColumn: Int = cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)
                val idColumn: Int = cursor.getColumnIndex(MediaStore.Video.Media._ID)
                val videoWidth = cursor.getColumnIndex(MediaStore.Video.Media.WIDTH)
                val bucketColumn: Int = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID)
                val durationColumn: Int = cursor.getColumnIndex(MediaStore.Video.Media.DURATION)
                val dateColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED)
                val dateModifiedColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED)
                do {
                    var fName = cursor.getString(folderName)
                    val thisId = cursor.getLong(idColumn)
                    val thisTitle = cursor.getString(titleColumn)
                    val bucket = cursor.getString(bucketColumn)
                    val duration: String? = cursor.getString(durationColumn)
                    val width = cursor.getString(videoWidth)
                    val dateAdded = cursor.getString(dateColumn)
                    val dateModified = cursor.getString(dateModifiedColumn)


                    if (bucket == folderId) {
                        if (duration != null) {
                            arraylistDuration[thisTitle] = duration

                            if (fName == null) {
                                fName = "Internal Storage"
                            }
                            arrayFolderName[thisTitle] = fName

                            arraylistId[thisTitle] = thisId.toString()
                            arraylistVideoWidth[thisTitle] = width
                            arrayListDate[thisTitle] = dateAdded
                            arrayListDateModified[thisTitle] = dateModified
                            bucketTitle = fName
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