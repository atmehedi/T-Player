package com.telent.t_player.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class ConnectionManager {
    fun checkConnectivity(context:Context):Boolean{
        val conectivityManager = context.getSystemService(Context
            .CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork:NetworkInfo? = conectivityManager.activeNetworkInfo
        if (activeNetwork?.isConnected !=null){
            return activeNetwork.isConnected
        }
        else{
            return false
        }
    }
}