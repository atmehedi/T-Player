package com.telent.t_player.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import com.telent.t_player.R


class SettingsActivity : AppCompatActivity() , AdapterView.OnItemSelectedListener{
   private lateinit var settingToolbar: androidx.appcompat.widget.Toolbar
   private lateinit var focusMode: SwitchMaterial
   private lateinit var sharedPreferences: SharedPreferences

   private lateinit var repeatMode:SwitchMaterial
    private lateinit var wakeMode:SwitchMaterial

    private var speedArray = arrayOf("1x","1.25x", "1.50x", "1.75x", "2x","0.25x", "0.50x")
    private var decoderArray = arrayOf("HW","SW")
    private lateinit var speedSpin:Spinner
    private lateinit var decodeSpin:Spinner


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initializer()


        sharedPreferences = getSharedPreferences(getString(R.string.shared_value_focus), Context.MODE_PRIVATE)
        focusMode.isUseMaterialThemeColors = true
        val focusCheck = sharedPreferences.getString("checked", "Unchecked")
        if (focusCheck == "checked") {
            focusMode.isChecked = true
        }
        val repeatCheck = sharedPreferences.getString("check_repeat", "repeat_false")
        if (repeatCheck == "repeat_true") {
            repeatMode.isChecked = true
        }

        val wakeCheck = sharedPreferences.getString("wake_status", "wake_true")
        if (wakeCheck == "wake_true") {
            wakeMode.isChecked = true
        }

        //focus mode control
        focusMode.setOnCheckedChangeListener { _, _ ->
            println(focusMode.isChecked)
            if (focusMode.isChecked) {
                sharedPreferences.edit().putString("checked", "checked").apply()
            } else {
                sharedPreferences.edit().putString("checked", "Unchecked").apply()
            }

        }
        //repeat mode control
        repeatMode.setOnCheckedChangeListener { _, _ ->
            if (repeatMode.isChecked) {
                sharedPreferences.edit().putString("check_repeat", "repeat_true").apply()
            } else {
                sharedPreferences.edit().putString("check_repeat", "repeat_false").apply()
            }

        }

        //wake mode control
        wakeMode.setOnCheckedChangeListener { _, _ ->
            if (wakeMode.isChecked) {
                sharedPreferences.edit().putString("wake_status", "wake_true").apply()
            } else {
                sharedPreferences.edit().putString("wake_status", "wake_false").apply()
            }

        }






        setUpToolbar()

        speedSpin.onItemSelectedListener = this
        decodeSpin.onItemSelectedListener = this






        val speedAdapter: ArrayAdapter<*> = ArrayAdapter<Any?>(this, android.R.layout.simple_spinner_item, speedArray)
        speedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        speedSpin.adapter = speedAdapter
        speedSpin.setSelection(2) // default selection

        val decoderAdapter: ArrayAdapter<*> = ArrayAdapter<Any?>(this, android.R.layout.simple_spinner_item, decoderArray)
        decoderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        decodeSpin.adapter = decoderAdapter

       
        decodeSpin.setSelection(decoderArray
                .indexOf(sharedPreferences.getString("decode_value","HW")))// default decoder selection


        speedSpin.setSelection(speedArray
                .indexOf(sharedPreferences.getString("speed_value","1x")+"x"))// default speed selection


    }

    private fun initializer() {
        settingToolbar = findViewById(R.id.toolbarSettings)
        focusMode = findViewById(R.id.F_mode)
        speedSpin = findViewById(R.id.speedSpinner)
        decodeSpin = findViewById(R.id.decoderSpinner)
        repeatMode = findViewById(R.id.repeat4all)
        wakeMode = findViewById(R.id.wakeMode)


    }

    private fun setUpToolbar() {
        setSupportActionBar(settingToolbar)
        supportActionBar?.title = getString(R.string.option_1)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //perform operation with selected value
        if (parent!!.id==R.id.decoderSpinner){
            when(position){
                0->{
                    sharedPreferences.edit().putString("decode_value", "HW").apply()

                }
                1->{
                    sharedPreferences.edit().putString("decode_value", "SW").apply()
                }
            }

        }
        else if(parent.id==R.id.speedSpinner){
            when (position) {
                0 -> {
                    sharedPreferences.edit().putString("speed_value", "1").apply()
                }
                1 -> {
                    sharedPreferences.edit().putString("speed_value", "1.25").apply()
                }
                2 -> {
                    sharedPreferences.edit().putString("speed_value", "1.50").apply()
                }
                3 -> {
                    sharedPreferences.edit().putString("speed_value", "1.75").apply()
                }
                4 -> {
                    sharedPreferences.edit().putString("speed_value","2").apply()
                }
                5 -> {
                    sharedPreferences.edit().putString("speed_value", "0.25").apply()
                }
                6 -> {
                    sharedPreferences.edit().putString("speed_value", "0.50").apply()
                }
                else -> {
                    println("spinner error")
                }
            }

        }

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        //empty operation
    }


}


