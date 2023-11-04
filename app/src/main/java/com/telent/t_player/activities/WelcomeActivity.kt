package com.telent.t_player.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.telent.t_player.R


class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1)
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun hasPermissions(): Boolean {
        for (permission in PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
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
                Handler(Looper.getMainLooper()).postDelayed({
                    Intent(this, MainActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
                }, 500)

            } else {
                finish()
            }
        }
    }

    companion object {
        private val PERMISSIONS: Array<String> = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }


}