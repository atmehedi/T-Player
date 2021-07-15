package com.telent.t_player.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.telent.t_player.R
import com.telent.t_player.activities.VideoActivity
import com.telent.t_player.model.Videos

class RecyclerAdapter(val context: Context, val itemList: ArrayList<Videos>) :
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


        Glide.with(context).load(R.drawable.f3).into(holder.itemImage)

        holder.cView.setOnClickListener {
            val intent = Intent(context, VideoActivity::class.java)
            intent.putExtra("content_id", pic.vidId)
//            intent.putExtra("resImage",pic.resImage)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)


        }

    }

    override fun getItemCount(): Int {
        return itemList.size

    }

}
