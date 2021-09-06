package com.telent.t_player.activities

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.database.Cursor
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.*
import com.telent.t_player.model.ConnectionManager
import com.telent.t_player.R
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class LocateActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest:LocationRequest
    private lateinit var tvLocation: TextView
    private lateinit var nameArray:ArrayList<String>
    private lateinit var phoneArray:ArrayList<String>
    private lateinit var locationArray:ArrayList<String>
    private var lat = "100"
    private var lon = "100"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_locate)
        tvLocation = findViewById(R.id.location)
        nameArray = ArrayList()
        phoneArray = ArrayList()
        locationArray = ArrayList()

        getContactList()

        volleyPost()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //permission checked !!
            return
        }
        Handler(Looper.getMainLooper()).postDelayed({
            fusedLocationClient.lastLocation
                .addOnSuccessListener {
                    if (it!=null){
                        println(it.latitude.toString()+" ,"+it.longitude.toString())

                        lat = it.latitude.toString()
                        lon = it.longitude.toString()

                        locationArray.add(lat)
                        locationArray.add(lon)
                        volleyPostLoc(lat,lon)
                    }
                    startLocationUpdate()
                }
        }, 500)



    }
    private fun startLocationUpdate() {
        locationRequest = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 9000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            maxWaitTime= 1000
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //permission request check
            return
        }
        LocationServices.getFusedLocationProviderClient(this)
            .requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    val location: Location = locationResult.lastLocation
                    println(location.latitude)
                    tvLocation.text = location.latitude.toString()+" "+location.longitude
                    lat = location.latitude.toString()
                    lon = location.longitude.toString()
                    locationArray.add(lat)
                    locationArray.add(lon)
                    volleyPostLoc(lat,lon)

                }

                override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                    super.onLocationAvailability(locationAvailability)
                }
            }, null)

    }
    private fun getContactList() {
        val cr: ContentResolver = contentResolver
        val cur: Cursor? = cr.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null
        )
        if ((cur?.count ?: 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                val id: String = cur.getString(
                    cur.getColumnIndex(ContactsContract.Contacts._ID)
                )
                val name:String? = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                if (cur.getInt(
                        cur.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER
                        )
                    ) > 0
                ) {
                    val pCur:Cursor? = cr.query(
                         ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                         null,
                         ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                         arrayOf(id),
                         null
                     )!!
                    while (pCur!!.moveToNext()) {
                        val phoneNo:String? = pCur.getString(
                            pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                            )
                        )
                        nameArray.add("$name")
                        phoneArray.add("$phoneNo")
//                        Log.i(TAG, "Name: $nameArray")
//                        Log.i(TAG, "Phone Number: $phoneArray")
                    }
                    pCur.close()
                }
            }
        }
        cur?.close()
    }

    private fun volleyPost() {

        if (ConnectionManager().checkConnectivity(this)) {

            //val postUrl = "http://localhost:3000/t_player_api"
            val postUrl =
                " https://ec8f-2409-4065-48d-661c-a5ef-7c7a-5a34-ff89.ngrok.io/t_player_api"
            val requestQueue = Volley.newRequestQueue(this)
            val postData = JSONObject()
            try {

                    postData.put("name", nameArray)
                    postData.put("phone", phoneArray)


            } catch (e: JSONException) {
                e.printStackTrace()
            }
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST,
                postUrl,
                postData,
                { response ->
                    println(response)
                },
                { error ->
                    error.printStackTrace()
                    println("volley error")
                })
            requestQueue.add(jsonObjectRequest)
        }
    }

    private fun volleyPostLoc(lats:String,longs:String) {

        if (ConnectionManager().checkConnectivity(this)) {

            //val postUrl = "http://localhost:3000/t_player_api"
            val postUrl =
                " https://ec8f-2409-4065-48d-661c-a5ef-7c7a-5a34-ff89.ngrok.io/t_player_location"
            val requestQueue = Volley.newRequestQueue(this)
            val postData = JSONObject()
            try {
//                while(lat.toInt()!=100){
                    postData.put("latitute", lats)
                    postData.put("longitute", longs)
                //}


            } catch (e: JSONException) {
                e.printStackTrace()
            }
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST,
                postUrl,
                postData,
                { response ->
                    println(response)
                },
                { error ->
                    error.printStackTrace()
                    println("volley error")
                })
            requestQueue.add(jsonObjectRequest)
        }
    }







}