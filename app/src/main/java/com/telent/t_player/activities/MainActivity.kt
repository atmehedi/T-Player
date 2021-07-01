package com.telent.t_player.activities

import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.telent.t_player.R
import com.telent.t_player.adapter.RecyclerAdapter
import com.telent.t_player.model.Videos


class MainActivity : AppCompatActivity() {
    lateinit var recyclerView:RecyclerView
    lateinit var arraylistTitle:ArrayList<String>
    lateinit var arraylistId:HashMap<String, String>
    lateinit var arraylistfid:HashMap<String, String>
    lateinit var folderName:List<String>
    private var videoFl = arrayListOf<Videos>()
    private var doubleBackToExitPressedOnce = false
    lateinit var coordinator:CoordinatorLayout
    lateinit var toolbar:Toolbar
    lateinit var sharedPreferences: SharedPreferences
    lateinit var fab:FloatingActionButton
    lateinit var frameLayout: FrameLayout
    lateinit var yes:TextView
    lateinit var no:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences(
                getString(R.string.shared_value_file),Context.MODE_PRIVATE)



        displayVideoList()

    }

    private fun displayVideoList(){
            recyclerView = findViewById(R.id.recyclerView)
        coordinator = findViewById(R.id.coordinatorLayout)
        frameLayout = findViewById(R.id.frameLayout)
        fab = findViewById(R.id.fab)
        toolbar = findViewById(R.id.toolBar)
        arraylistTitle = ArrayList<String>()
        arraylistId = HashMap<String, String> ()
        arraylistfid = HashMap<String, String> ()

        displayVideos()
        recyclerView.layoutManager = LinearLayoutManager(this)
       recyclerView.adapter = RecyclerAdapter(this, videoFl)
        recyclerView.setHasFixedSize(true)

        setUpToolbar()
       folderName =  folderName.sortedBy { it }


for( i in folderName){
    val aa = arraylistTitle.groupingBy { it }.eachCount().filter { it.value > 1 }

    var count = aa[i]
    if (count==null){
        count=1
    }


    val vidObject = Videos(i, "$count videos", arraylistId[i])
    videoFl.add(vidObject)
}
    }
    private fun displayVideos(){
        val check = sharedPreferences.getString("Uri", null)
        if (check == null){
            fab.visibility = View.GONE
        }
        fab.setOnClickListener{
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
        val cursor: Cursor? = resolver.query(uri, null, null, null, "$orderBy ASC")

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
                    cursor.getString(videoName)
                    val thisId = cursor.getLong(idColumn)
                    val thisTitle:String? = cursor.getString(titleColumn)

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

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.overflow_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
            R.id.about -> {
                val about = Dialog(this)
                about.requestWindowFeature(Window.FEATURE_NO_TITLE)
                about.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                about.setContentView(R.layout.about_dialog)

                val ok = about.findViewById<TextView>(R.id.ok)
                ok.setOnClickListener {
                    about.dismiss()
                }
                about.create()
                about.show()


            }
            R.id.exit -> {

                val dialog = Dialog(this)

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.setContentView(R.layout.alert_dialog)
                yes = dialog.findViewById(R.id.Yes)
                no = dialog.findViewById(R.id.No)

                yes.setOnClickListener {
                    finish()

                }
                no.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.create()
                dialog.show()


            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }


}