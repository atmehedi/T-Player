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
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.telent.t_player.R
import com.telent.t_player.adapter.RecyclerAdapter
import com.telent.t_player.model.Videos


class MainActivity : AppCompatActivity() {
    lateinit var recyclerView:RecyclerView
    lateinit var arraylistTitle:ArrayList<String>
    lateinit var arraylistId:HashMap<String,String>
    lateinit var arraylistfid:HashMap<String,String>
    lateinit var folderName:List<String>
    private var videoFl = arrayListOf<Videos>()
    lateinit var coordinator:CoordinatorLayout
    lateinit var frameLayout: FrameLayout
    lateinit var toolbar:Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        displayVideoList()

    }

    private fun displayVideoList(){
            recyclerView = findViewById(R.id.recyclerView)
        coordinator = findViewById(R.id.coordinatorLayout)
        frameLayout = findViewById(R.id.frameLayout)
        toolbar = findViewById(R.id.toolBar)
        arraylistTitle = ArrayList<String>()
        arraylistId = HashMap<String, String> ()
        arraylistfid = HashMap<String, String> ()
        displayVideos()
        recyclerView.layoutManager = LinearLayoutManager(this)
       recyclerView.adapter = RecyclerAdapter(this,videoFl)
        recyclerView.setHasFixedSize(true)

        setUpToolbar()
for( i in folderName){
    val aa = arraylistTitle.groupingBy { it }.eachCount().filter { it.value > 1 }

    var count = aa[i]
    if (count==null){
        count=1
    }


    val vidObject = Videos(i, "$count videos",arraylistId[i])
    videoFl.add(vidObject)
}
    }
    fun displayVideos(){

        val resolver: ContentResolver = contentResolver
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val orderBy = MediaStore.Video.Media.DEFAULT_SORT_ORDER
        val cursor: Cursor? = resolver.query(uri, null, null, null, orderBy)

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
                val videoName = cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)
                val titleColumn: Int = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
                val idColumn: Int = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID)
                do {
                    val videoName = cursor.getString(videoName)
                    val thisId = cursor.getLong(idColumn)
                    var thisTitle:String? = cursor.getString(titleColumn)

                    if (thisTitle != null) {
                        arraylistTitle.add(thisTitle)
                        arraylistId[thisTitle] = thisId.toString()
                    }
                    if (thisTitle==null){
                        arraylistTitle.add("Internal Storage")
                        arraylistId["Internal Storage"] = thisId.toString()
                    }

                     folderName = arraylistTitle.distinct()
                } while (cursor.moveToNext())
            }
        }
        cursor?.close()

    }
    private fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.folderTop)
        //supportActionBar?.setHomeButtonEnabled(true)
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

}