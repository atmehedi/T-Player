package com.telent.t_player.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.telent.t_player.R
import com.telent.t_player.activities.VideoActivity
import com.telent.t_player.model.Videos


class RecyclerAdapter(val activity: Activity, private val itemList: ArrayList<Videos>) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val itemName: TextView = itemView.findViewById(R.id.txt_videoTitle)
        val itemImage: ImageView = itemView.findViewById(R.id.img_folder)
        val itemCount: TextView = itemView.findViewById(R.id.txt_videoCount)
        val cView: androidx.cardview.widget.CardView = itemView.findViewById(R.id.cardView)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.sing_row_item, parent, false)


        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pic = itemList[position]
        holder.itemName.text = pic.vidTitle
        holder.itemCount.text = pic.VidCount


        Glide.with(activity).load(R.drawable.ic_baseline_folder_24).into(holder.itemImage)

        val itemClick = AlphaAnimation(1f, 0.8f)


        holder.cView.setOnClickListener {
            holder.cView.startAnimation(itemClick)
            val intent = Intent(activity, VideoActivity::class.java)
            intent.putExtra("content_id", pic.vidId)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)


        }


    }

    override fun getItemCount(): Int {
        return itemList.size

    }

}
