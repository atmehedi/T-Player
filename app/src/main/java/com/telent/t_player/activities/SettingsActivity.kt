package com.telent.t_player.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import com.telent.t_player.R


class SettingsActivity : AppCompatActivity() {
    lateinit var settingToolbar: androidx.appcompat.widget.Toolbar
    lateinit var focusMode: SwitchMaterial
    lateinit var sharedPreferences: SharedPreferences


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
        focusMode.setOnCheckedChangeListener { _, _ ->
            println(focusMode.isChecked)
            if (focusMode.isChecked) {
                sharedPreferences.edit().putString("checked", "checked").apply()
                println("value kept")
            } else {
                sharedPreferences.edit().putString("checked", "Unchecked").apply()
                println("value removed")
            }

        }
        setUpToolbar()


    }

    private fun initializer() {
        settingToolbar = findViewById(R.id.toolbarSettings)
        focusMode = findViewById(R.id.F_mode)

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


}


