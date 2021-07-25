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
import android.os.Looper
import android.provider.MediaStore
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.telent.t_player.R
import com.telent.t_player.adapter.RecyclerAdapter
import com.telent.t_player.model.Videos
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var arraylistTitle: ArrayList<String>
    private lateinit var arrayListDate: HashMap<String, String>
    private lateinit var arrayListDateModified: HashMap<String, String>
    private lateinit var arraylistId: HashMap<String, String>
    private lateinit var arraylistfid: HashMap<String, String>
    private lateinit var folderName: List<String>
    private var videoFl = arrayListOf<Videos>()
    private var doubleBackToExitPressedOnce = false
    private lateinit var coordinator: CoordinatorLayout
    private lateinit var toolbar: Toolbar
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var fab: FloatingActionButton
    private lateinit var frameLayout: FrameLayout
    private lateinit var yes: TextView
    private lateinit var no: TextView

    private lateinit var apply: TextView
    private lateinit var cancel: TextView

    private lateinit var swipeDown: SwipeRefreshLayout

    private lateinit var vidObject: Videos

    private lateinit var radioGroup:RadioGroup
    private lateinit var radioGroup2:RadioGroup
    private lateinit var radioDate:RadioButton
    private lateinit var radioname:RadioButton
    private lateinit var radioModified:RadioButton

    private lateinit var radioAsc:RadioButton
    private lateinit var radioDesc:RadioButton



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)




        sharedPreferences = getSharedPreferences(
                getString(R.string.shared_value_file), Context.MODE_PRIVATE)



        if (intent != null) {
            val exit = intent.getStringExtra("exit")
            if (exit == "exit") {
                finish()
            }
        }


        swipeDown = findViewById(R.id.swipeDown)


        displayVideoList()
        swipeDown.setOnRefreshListener {
            videoFl.clear()
            displayVideoList()

            swipeDown.isRefreshing = false
        }


    }


    private fun displayVideoList() {
        recyclerView = findViewById(R.id.recyclerView)
        coordinator = findViewById(R.id.coordinatorLayout)
        frameLayout = findViewById(R.id.frameLayout)
        fab = findViewById(R.id.fab)



        toolbar = findViewById(R.id.toolBar)
        arraylistTitle = ArrayList()
        arrayListDate = HashMap()
        arrayListDateModified = HashMap()
        arraylistId = HashMap()
        arraylistfid = HashMap()

        folderName = listOf()

        displayVideos()




        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RecyclerAdapter(this, videoFl)
        recyclerView.setHasFixedSize(true)



        setUpToolbar()
         // folderName = folderName.sortedBy { it }



        for (i in folderName) {
            val aa = arraylistTitle.groupingBy { it }.eachCount().filter { it.value > 1 }

            var count = aa[i]
            if (count == null) {
                count = 1
            }


            vidObject = Videos(i, "$count videos", arraylistId[i], arrayListDate[i], arrayListDateModified[i])
            videoFl.add(vidObject)


        }


        sortingFunction()

    }

    private fun sortingFunction() {     //APPLYING SORT SELECTION

      val sortValue =  sharedPreferences.getString("sortValue","byName")
        val sortType = sharedPreferences.getString("sortType","byName")

        when (sortValue) {
            "Sort By date" -> {
                videoFl.sortWith(compareBy { it.dateAdded })
            }
            "Sort By Last modified" -> {
                videoFl.sortWith(compareBy {it.dateModified})
            }
            "Sort By name" -> {
                videoFl.sortWith(compareBy { it.vidTitle })
            }
            else -> {
                videoFl.sortWith(compareBy { it.vidTitle })
            }
        }
            if (sortType =="Descending"){
            videoFl.reverse()
                }

    }

    private fun displayVideos(): Cursor {
        val check = sharedPreferences.getString("Uri", null)
        if (check == null) {
            fab.visibility = View.GONE
        }

        fab.setOnClickListener {
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
                val dateColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED)
                val dateModifiedColumn  = cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED)
                do {
                    cursor.getString(videoName)
                    val thisId = cursor.getLong(idColumn)
                    val thisTitle: String? = cursor.getString(titleColumn)
                    val dateAdded = cursor.getString(dateColumn)
                    val dateModified = cursor.getString(dateModifiedColumn)

                    if (thisTitle != null) {
                        arraylistTitle.add(thisTitle)
                        arraylistId[thisTitle] = thisId.toString()
                        arrayListDate[thisTitle] = dateAdded
                        arrayListDateModified[thisTitle] = dateModified

                    }
                    if (thisTitle == null) {
                        arraylistTitle.add("Internal Storage")
                        arraylistId["Internal Storage"] = thisId.toString()
                    }

                    folderName = arraylistTitle.distinct()
                } while (cursor.moveToNext())
            }
        }
//        cursor.setNotificationUri(getContext().getContentResolver(), uri);
//        return cursor;

        cursor!!.setNotificationUri(this.contentResolver, uri)
        cursor.close()
        return cursor

    }

    private fun setUpToolbar() {
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
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
            }
            R.id.about -> {
                val about = Dialog(this,R.style.PauseDialog)
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

                val dialog = Dialog(this,R.style.PauseDialog)

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
// sort functionality
            R.id.sort -> {
                val dialog = Dialog(this,R.style.PauseDialog)

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.setContentView(R.layout.sort_dialog)
                apply = dialog.findViewById(R.id.apply)
                cancel = dialog.findViewById(R.id.cancel)
                radioGroup = dialog.findViewById(R.id.radioGroup)
                radioGroup2 = dialog.findViewById(R.id.radioGroup2)

                radioDesc = dialog.findViewById(R.id.desc1)
                radioAsc = dialog.findViewById(R.id.asc)

                radioModified = dialog.findViewById(R.id.modified)
                radioname = dialog.findViewById(R.id.nameBy)
                radioDate = dialog.findViewById(R.id.date1)

                defaultRadioCheck()

                  radioGroup.setOnCheckedChangeListener { group, checkedId ->

                    group.findViewById<View>(checkedId) as RadioButton
                }
                radioGroup2.setOnCheckedChangeListener { group, checkedId ->

                    group.findViewById<View>(checkedId) as RadioButton
                }
                apply.setOnClickListener {
                    val selectedId = radioGroup.checkedRadioButtonId
                    val selectedId2 = radioGroup2.checkedRadioButtonId
                    if (selectedId == -1) {
                       println("no option selected")
                    } else {
                        val radioButton = radioGroup
                                .findViewById<View>(selectedId) as RadioButton

                        radioButton.isChecked = true
                        sharedPreferences.edit().putString("sortValue",radioButton.text.toString()).apply()


                    }
                    if (selectedId2 == -1) {
                        Toast.makeText(this@MainActivity,
                                "No option selected",
                                Toast.LENGTH_SHORT)
                                .show()
                    } else {
                        val radioButton2 = radioGroup2
                                .findViewById<View>(selectedId2) as RadioButton
                        sharedPreferences.edit().putString("sortType",radioButton2.text.toString()).apply()

                    }
                    startActivity(intent)
                    finish()

                }

                cancel.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.create()
                dialog.show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun defaultRadioCheck() {
        val sortValue =  sharedPreferences.getString("sortValue","byName")
        val sortType = sharedPreferences.getString("sortType","byName")

        when (sortValue) {
            "Sort By date" -> {
                radioGroup.check(radioDate.id)
            }
            "Sort By Last modified" -> {
                radioGroup.check(radioModified.id)
            }
            "Sort By name" -> {
                radioGroup.check(radioname.id)
            }
            else -> {
                radioGroup.check(radioname.id)
            }
        }
        if (sortType =="Descending"){
            radioGroup2.check(radioDesc.id)
        }else{
            radioGroup2.check(radioAsc.id)
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }


        doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }


}
