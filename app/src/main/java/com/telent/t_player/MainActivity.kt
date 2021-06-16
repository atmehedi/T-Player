package com.telent.t_player

import android.content.ContentResolver
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.telent.t_player.adapter.RecyclerAdapter
import com.telent.t_player.model.Videos


class MainActivity : AppCompatActivity() {
    lateinit var recyclerView:RecyclerView
    lateinit var arraylistTitle:ArrayList<String>
    lateinit var arraylistId:HashMap<String,String>
    lateinit var folderName:List<String>
    private var videoFl = arrayListOf<Videos>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        displayVideoList()

    }

    fun displayVideoList(){
            recyclerView = findViewById(R.id.recyclerView)
        arraylistTitle = ArrayList<String>()
        arraylistId = HashMap<String, String> ()
        displayVideos()
        recyclerView.layoutManager = LinearLayoutManager(this)
       recyclerView.adapter = RecyclerAdapter(this,videoFl)
        recyclerView.setHasFixedSize(true)


for( i in folderName){
    val aa = arraylistTitle.groupingBy { it }.eachCount().filter { it.value > 1 }

    var count = aa[i]
    if (count==null){
        count=1
    }


    val vidObject = Videos(i, "$count videos",arraylistId[i])
    videoFl.add(vidObject)
}

      //  println(videoFl)
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

                val titleColumn: Int = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
                val idColumn: Int = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID)
                do {
                    val thisId = cursor.getLong(idColumn)
                    val thisTitle = cursor.getString(titleColumn)

                    arraylistTitle.add(thisTitle)
                    arraylistId[thisTitle] = thisId.toString()

                     folderName = arraylistTitle.distinct()
                } while (cursor.moveToNext())
            }
        }
        cursor?.close()

    }

}