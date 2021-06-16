package com.telent.t_player

import android.content.ContentResolver
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.telent.t_player.adapter.VideoRecyclerAdapter
import com.telent.t_player.model.VideoModel

class VideoActivity : AppCompatActivity() {
    lateinit var videoRecyclerView:RecyclerView
    private var videoAr = arrayListOf<VideoModel>()
    lateinit var arraylistTitle:ArrayList<String>
    lateinit var folderName:List<String>
    lateinit var arraylistId:HashMap<String, String>
    lateinit var arraylistduration:HashMap<String, String>
    var folderId:String? = "folderId"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        listMain()

    }

    fun listMain(){
        if (intent !=null){
            folderId = intent.getStringExtra("content_id")
            if (folderId==null){
                println("folder is is missing")
            }
        }else{
            println("intent is empty")
        }
        videoRecyclerView = findViewById(R.id.recyclerViewVideo)
        arraylistTitle = ArrayList<String>()
        arraylistId = HashMap<String, String>()
        arraylistduration = HashMap<String, String>()
        folderName = ArrayList<String>()
        videosList()
        videoRecyclerView .layoutManager = LinearLayoutManager(this)
        videoRecyclerView .adapter = VideoRecyclerAdapter(this, videoAr)
        videoRecyclerView .setHasFixedSize(true)

  for( i in folderName){
            val ttt:Int? = arraylistduration[i]?.toInt()
            val mm: Int = (ttt?.div(60000) ?: 10) % 60000
            val ss: Int = (ttt?.rem(60000) ?: 10) / 1000

            val runTime = String.format("%02d:%02d", mm, ss)


            val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString()+"/"+arraylistId[i]


            val vidObject = VideoModel(i, runTime, uri)
            videoAr.add(vidObject)
        }

    }
    fun videosList(){

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

                val titleColumn: Int = cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)
                val idColumn: Int = cursor.getColumnIndex(MediaStore.Video.Media._ID)
                val bucketColumn: Int = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID)
                 val durationColumn: Int =cursor.getColumnIndex(MediaStore.Video.Media.DURATION)
                do {
                    val thisId = cursor.getLong(idColumn)
                    val thisTitle = cursor.getString(titleColumn)
                    val bucket = cursor.getString(bucketColumn)
                    val duration: String? = cursor.getString(durationColumn)




                    if (bucket==folderId){
                        if (duration != null) {
                            arraylistduration.put(thisTitle, duration)
                            arraylistId.put(thisTitle,thisId.toString())
                        }
                        arraylistTitle.add(thisTitle)

                        folderName = arraylistTitle
                    }


                } while (cursor.moveToNext())
            }
        }
        cursor?.close()

    }

}