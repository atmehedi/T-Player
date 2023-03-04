package com.telent.t_player.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.os.HandlerCompat.postDelayed
import com.telent.t_player.R


class WelcomeActivity : AppCompatActivity(){

    private lateinit var PERMISSIONS: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        PERMISSIONS = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE

        )
        if (!hasPermissions(this, *PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1)
        }else{
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }
    private fun hasPermissions(context: Context?, vararg PERMISSIONS: String): Boolean {
        if (context != null) {
            for (permission in PERMISSIONS) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                {
                    return false
                }
            }
        }
        return true
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(this,MainActivity::class.java)
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 500)

            } else {
               finish()
            }

        }
    }


}